package com.sparta.adjustment.batch;

import com.sparta.adjustment.batch.faultTolerant.DayAggregationSkipPolicy;
import com.sparta.adjustment.batch.listener.MyJobExecutionListener;
import com.sparta.adjustment.batch.listener.MyStepExecutionListener;
import com.sparta.adjustment.batch.partitioner.CheckAggregationForUpdateAdjustmentPartitioner;
import com.sparta.adjustment.batch.partitioner.CheckHistoryForCreateAggregationPartitioner;
import com.sparta.adjustment.batch.processor.CreateAggregationProcessor;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.adjustment.repository.AdjustmentRepository;
import com.sparta.adjustment.domain.adjustment.repository.AggregationRepository;
import com.sparta.adjustment.domain.video.DayVideoLog;
import com.sparta.adjustment.domain.video.repository.DayVideoLogRepository;
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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class DayAggregationBatch extends DefaultBatchConfiguration {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerFactory entityManagerFactory;
    private final AggregationRepository aggregationRepository;
    private final AdjustmentRepository adjustmentRepository;
    private final DayVideoLogRepository dayVideoLogRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private int poolSize;

    @Value("${poolSize:5}")
    public void setPoolSize(int poolSize){
        this.poolSize = poolSize;
    }
    @Bean
    public TaskExecutor batchTaskExecutor(){
        return new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(poolSize)
                .maxPoolSize(poolSize)
                .threadNamePrefix("partition-thread")
                .build();
    }
    @Bean
    public Job aggregationProcessingJob(JobRepository jobRepository,
                                 Step createAggregationMasterStep,
                                 Step updateAdjustmentMasterStep){
        return new JobBuilder("aggregationProcessingJob", jobRepository)
                .start(createAggregationMasterStep)
                .next(updateAdjustmentMasterStep)
                .listener(new MyJobExecutionListener())
                .build();
    }

    //step1
    @Bean
    public Step createAggregationMasterStep(JobRepository jobRepository){
        return new StepBuilder("step1.master", jobRepository)
                .partitioner("step1", createAggregationPartitioner(null))
                .partitionHandler(createAggregationPartitionHandler(jobRepository))
                .listener(new MyStepExecutionListener())
                .build();
    }
    @Bean
    @StepScope
    public CheckHistoryForCreateAggregationPartitioner createAggregationPartitioner(
            @Value("#{jobParameters[referenceDate]}") String referenceDate){
        LocalDateTime startTime = LocalDate.parse(referenceDate).atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1);
        return new CheckHistoryForCreateAggregationPartitioner(dayVideoLogRepository, startTime, endTime);
    }
    @Bean
    public TaskExecutorPartitionHandler createAggregationPartitionHandler(JobRepository jobRepository) {
        TaskExecutorPartitionHandler partitionHandler
                = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(processHistoryForAggregation(jobRepository));
        partitionHandler.setTaskExecutor(batchTaskExecutor());
        partitionHandler.setGridSize(poolSize);

        return partitionHandler;
    }
    @Bean
    public Step processHistoryForAggregation(JobRepository jobRepository){
        return new StepBuilder("step1", jobRepository)
                .<DayVideoLog, Aggregation> chunk(100, platformTransactionManager)
                .reader(dayVideoLogItemReader(null, null, null))
                .processor(aggregationItemProcessor(null))
                .writer(aggregationDataWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DayVideoLog> dayVideoLogItemReader(
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId,
            @Value("#{jobParameters[referenceDate]}") String referenceDate){

        LocalDateTime start = LocalDate.parse(referenceDate).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Map<String, Object> params = new HashMap<>();
        params.put("minId", minId);
        params.put("maxId", maxId);
        params.put("start", start);
        params.put("end", end);

        return new JpaPagingItemReaderBuilder<DayVideoLog>()
                .name("dayVideoLogItemReader")
                .pageSize(100)
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT d " +
                        "FROM DayVideoLog d " +
                        "WHERE d.videoId >= :minId " +
                        "AND d.videoId <= :maxId " +
                        "AND d.createdAt >= :start " +
                        "AND d.createdAt < :end " +
                        "ORDER BY d.id")
                .parameterValues(params)
                .build();

    }

//    @Bean
//    @StepScope
//    public JdbcPagingItemReader<DayVideoLog> dayVideoLogItemReader(
//            @Value("#{stepExecutionContext[minId]}") Long minId,
//            @Value("#{stepExecutionContext[maxId]}") Long maxId,
//            @Value("#{jobParameters[referenceDate]}") String referenceDate){
//        Map<String, Object> params = new HashMap<>();
//        params.put("minId", minId);
//        params.put("maxId", maxId);
//        params.put("referenceDate", referenceDate);
//
//        return new JdbcPagingItemReaderBuilder<DayVideoLog>()
//                .name("dayVideoLogItemReader")
//                .dataSource(dataSource)
//                .pageSize(100)
//                .selectClause("SELECT *")
//                .fromClause("FROM day_video_log")
//                .whereClause("WHERE id BETWEEN :minId AND :maxId " +
//                        "AND createdAt >= to_date(:referenceDate, 'YYYY-MM-DD') " +
//                        "AND createdAt < to_date(:referenceDate, 'YYYY-MM-DD') + interval '1 day'")
//                .sortKeys(Collections.singletonMap("id", Order.ASCENDING))
//                .parameterValues(params)
//                .rowMapper(new BeanPropertyRowMapper<>(DayVideoLog.class))
//                .build();
//    }
    @Bean
    @StepScope
    public ItemProcessor<DayVideoLog, Aggregation> aggregationItemProcessor(
            @Value("#{jobParameters[referenceDate]}") String referenceDate){
        return new CreateAggregationProcessor(referenceDate, jdbcTemplate, adjustmentRepository);
    }
    @Bean
    public JdbcBatchItemWriter<Aggregation> aggregationDataWriter(){
        System.out.println();
        return new JdbcBatchItemWriterBuilder<Aggregation>()
                .dataSource(dataSource)
                .sql("Insert into aggregation" +
                        "(video_id, views_amount, ad_amount, views, ad_views, viewing_time, created_at, modified_at)" +
                        "values " +
                        "(:videoId, :viewsAmount, :adAmount, :views, :adViews, :viewingTime, now(), now())")
                .beanMapped().build();
    }

    //step2
    @Bean
    public Step updateAdjustmentMasterStep(JobRepository jobRepository){
        return new StepBuilder("step2.master", jobRepository)
                .partitioner("step2", updateAdjustmentPartitioner(null))
                .partitionHandler(updateAdjustmentPartitionHandler(jobRepository))
                .listener(new MyStepExecutionListener())
                .build();
    }
    @Bean
    @StepScope
    public CheckAggregationForUpdateAdjustmentPartitioner updateAdjustmentPartitioner(
            @Value("#{jobParameters[currentDate]}") String currentDate){
        return new CheckAggregationForUpdateAdjustmentPartitioner(aggregationRepository, currentDate);
    }
    @Bean
    public TaskExecutorPartitionHandler updateAdjustmentPartitionHandler(JobRepository jobRepository) {
        TaskExecutorPartitionHandler partitionHandler
                = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(updateAdjustDataStep(jobRepository));
        partitionHandler.setTaskExecutor(batchTaskExecutor());
        partitionHandler.setGridSize(poolSize);

        return partitionHandler;
    }
    @Bean
    public Step updateAdjustDataStep(JobRepository jobRepository){
        return new StepBuilder("step2", jobRepository)
                .<Aggregation, Aggregation> chunk(100, platformTransactionManager)
                .reader(aggregationJpaItemReader(null, null, null))
                .writer(adjustmentDataWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
    }
    @Bean
    @StepScope
    public JpaPagingItemReader<Aggregation> aggregationJpaItemReader(
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId,
            @Value("#{jobParameters[currentDate]}") String currentDate){

        LocalDateTime start = LocalDate.parse(currentDate).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Map<String, Object> params = new HashMap<>();
        params.put("minId", minId);
        params.put("maxId", maxId);
        params.put("start", start);
        params.put("end", end);

        return new JpaPagingItemReaderBuilder<Aggregation>()
                .name("aggregationJpaReader")
                .pageSize(100)
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a " +
                        "FROM Aggregation a " +
                        "WHERE a.id >= :minId AND a.id <= :maxId " +
                        "AND a.createdAt >= :start " +
                        "AND a.createdAt < :end")
                .parameterValues(params)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Aggregation> adjustmentDataWriter(){
        return new JdbcBatchItemWriterBuilder<Aggregation>()
                .dataSource(dataSource)
                .sql("UPDATE adjustment " +
                        "SET " +
                        "    total_views = total_views + :views, " +
                        "    total_ad_views = total_ad_views + :adViews, " +
                        "    total_play_time = total_play_time + :viewingTime, " +
                        "    total_amount = total_amount + :viewsAmount + :adAmount, " +
                        "    modified_at = now() " +
                        "WHERE id = :videoId"
                ).beanMapped().build();
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(super.getDataSource());
    }
}
