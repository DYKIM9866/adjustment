package com.sparta.adjustment.batch;

import com.sparta.adjustment.batch.faultTolerant.DayAggregationSkipPolicy;
import com.sparta.adjustment.batch.processor.CreateAggregationProcessor;
import com.sparta.adjustment.domain.adjustment.Adjustment;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import feign.RetryableException;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;


@Configuration
@RequiredArgsConstructor
public class DayAggregationBatch extends DefaultBatchConfiguration {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerFactory entityManagerFactory;

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(super.getDataSource());
    }

    @Bean
    public Job dayAggregationJob(JobRepository jobRepository,
                                 Step checkHistoryForCreateAggregation,
                                 Step checkAggregationForUpdateAdjustment){
        return new JobBuilder("dayAggregationJob", jobRepository)
                .start(checkHistoryForCreateAggregation)
                .next(checkAggregationForUpdateAdjustment)
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
    public Step checkHistoryForCreateAggregation(JobRepository jobRepository){
        return new StepBuilder("step1", jobRepository)
                .<Adjustment, Aggregation> chunk(100, getTransactionManager())
                .reader(adjustmentJdbcReader())
                .processor(processor(null))
                .writer(writer())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
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
    public JdbcPagingItemReader<Adjustment> adjustmentJdbcReader(){
        return new JdbcPagingItemReaderBuilder<Adjustment>()
                .name("adjustmentReader")
                .dataSource(dataSource)
                .pageSize(100)
                .selectClause("SELECT *")
                .fromClause("FROM adjustment")
                .sortKeys(Collections.singletonMap("id", Order.ASCENDING))
                .rowMapper(new BeanPropertyRowMapper<>(Adjustment.class))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Adjustment, Aggregation> processor(
            @Value("#{jobParameters[referenceDate]}") String referenceDate){
        return new CreateAggregationProcessor(referenceDate, jdbcTemplate);
    }

    @Bean
    public JdbcBatchItemWriter<Aggregation> writer(){
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

//    @Bean
//    public JdbcPagingItemReader<Aggregation> aggregationReader(){
//        return new JdbcPagingItemReaderBuilder<Aggregation>()
//                .name("aggregationReader")
//                .dataSource(dataSource)
//                .pageSize(100)
//                .selectClause("SELECT id, video_id, views_amount, ad_amount, views, ad_views , viewing_time")
//                .fromClause("FROM aggregation ")
////                .whereClause("WHERE to_char(created_at,'YYYY-MM-DD') = '" + CURRENT_DATE.toString() + "'")
////                .parameterValues(Collections.singletonMap("created_at", CURRENT_DATE.toString()))
//                .sortKeys(Collections.singletonMap("id", Order.ASCENDING))
//                .beanRowMapper(Aggregation.class)
//                .build();
//    }
}
