package com.sparta.adjustment.batch;

import com.sparta.adjustment.batch.faultTolerant.DayAggregationSkipPolicy;
import com.sparta.adjustment.batch.listener.MyJobExecutionListener;
import com.sparta.adjustment.batch.listener.MyStepExecutionListener;
import com.sparta.adjustment.batch.partitioner.CheckHistoryForCreateAggregationPartitioner;
import com.sparta.adjustment.batch.processor.CreateAggregationProcessor;
import com.sparta.adjustment.domain.adjustment.Adjustment;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.adjustment.repository.AdjustmentRepository;
import feign.RetryableException;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class DayAggregationBatch extends DefaultBatchConfiguration {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerFactory entityManagerFactory;
    private final AdjustmentRepository adjustmentRepository;


    private int poolSize;
    @Value("${poolSize:5}")
    public void setPoolSize(int poolSize){
        this.poolSize = poolSize;
    }

    @Bean
    public Job dayAggregationJob(JobRepository jobRepository,
                                 Step step1Master,
                                 Step checkAggregationForUpdateAdjustment){
        return new JobBuilder("dayAggregationJob", jobRepository)
                .start(step1Master)
                .next(checkAggregationForUpdateAdjustment)
                .listener(new MyJobExecutionListener())
                .build();
    }

    @Bean
    public Step step1Master(JobRepository jobRepository){
        return new StepBuilder("step1.master", jobRepository)
                .partitioner("step1", step1Partitioner())
                .partitionHandler(step1PartitionHandler(jobRepository))
                .listener(new MyStepExecutionListener())
                .build();
    }


    @Bean
    public Step checkHistoryForCreateAggregation(JobRepository jobRepository){
        return new StepBuilder("step1", jobRepository)
                .<Adjustment, Aggregation> chunk(100, getTransactionManager())
                .reader(jdbcAdjustmentReader(null, null))
                .processor(createAggregationProcessor(null))
                .writer(aggregationWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler step1PartitionHandler(JobRepository jobRepository) {
        TaskExecutorPartitionHandler partitionHandler
                = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(checkHistoryForCreateAggregation(jobRepository));
        partitionHandler.setTaskExecutor(executor());
        partitionHandler.setGridSize(poolSize);

        return partitionHandler;
    }
    @Bean
    public Step checkAggregationForUpdateAdjustment(JobRepository jobRepository){
        return new StepBuilder("step2", jobRepository)
                .<Aggregation, Aggregation> chunk(100, getTransactionManager())
                .reader(aggregationJpaReader(null))
                .writer(adjustmentWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
    }

    @Bean
    public TaskExecutor executor(){
        return new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(poolSize)
                .maxPoolSize(poolSize)
                .threadNamePrefix("partition-thread")
                .build();
    }

    @Bean
    public CheckHistoryForCreateAggregationPartitioner step1Partitioner(){
        return new CheckHistoryForCreateAggregationPartitioner(adjustmentRepository);
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Adjustment> jdbcAdjustmentReader(
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId){
        Map<String, Object> params = new HashMap<>();
        params.put("minId", minId);
        params.put("maxId", maxId);

        return new JdbcPagingItemReaderBuilder<Adjustment>()
                .name("adjustmentReader")
                .dataSource(dataSource)
                .pageSize(100)
                .selectClause("SELECT *")
                .fromClause("FROM adjustment")
                .whereClause("id BETWEEN :minId AND :maxId")
                .sortKeys(Collections.singletonMap("id", Order.ASCENDING))
                .parameterValues(params)
                .rowMapper(new BeanPropertyRowMapper<>(Adjustment.class))
                .build();
    }

    @Bean
    public Job testStep1Job(JobRepository jobRepository){
        return new JobBuilder("step1", jobRepository)
                .start(checkHistoryForCreateAggregation((jobRepository)))
                .build();
    }

    @Bean
    public Job testStep2Job(JobRepository jobRepository){
        return new JobBuilder("step2", jobRepository)
                .start(checkAggregationForUpdateAdjustment((jobRepository)))
                .build();
    }


    @Bean
    @StepScope
    public ItemProcessor<Adjustment, Aggregation> createAggregationProcessor(
            @Value("#{jobParameters[referenceDate]}") String referenceDate){
        return new CreateAggregationProcessor(referenceDate, jdbcTemplate);
    }

    @Bean
    public JdbcBatchItemWriter<Aggregation> aggregationWriter(){
        return new JdbcBatchItemWriterBuilder<Aggregation>()
                .dataSource(dataSource)
                .sql("Insert into aggregation" +
                        "(video_id, views_amount, ad_amount, views, ad_views, viewing_time, created_at, modified_at)" +
                        "values " +
                        "(:videoId, :viewsAmount, :adAmount, :views, :adViews, :viewingTime, now(), now())")
                .beanMapped().build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Aggregation> aggregationJpaReader(
            @Value("#{jobParameters[currentDate]}") String currentDate){
        return new JpaPagingItemReaderBuilder<Aggregation>()
                .name("aggregationJpaReader")
                .pageSize(100)
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Aggregation a WHERE " +
                        "FUNCTION('to_char', a.createdAt,'YYYY-MM-DD') = :date ")
                .parameterValues(Collections.singletonMap("date",currentDate))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Aggregation> adjustmentWriter(){
        return new JdbcBatchItemWriterBuilder<Aggregation>()
                .dataSource(dataSource)
                .sql("UPDATE adjustment " +
                        "SET " +
                        "    total_views = total_views + :views, " +
                        "    total_ad_views = total_ad_views + :adViews, " +
                        "    total_play_time = total_play_time + :viewingTime, " +
                        "    total_amount = total_amount + :viewsAmount + :adAmount " +
                        "WHERE id = :videoId"
                ).beanMapped().build();
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(super.getDataSource());
    }
}
