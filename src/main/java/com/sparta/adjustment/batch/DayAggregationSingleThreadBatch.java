package com.sparta.adjustment.batch;

import com.sparta.adjustment.batch.faultTolerant.DayAggregationSkipPolicy;
import com.sparta.adjustment.batch.listener.MyJobExecutionListener;
import com.sparta.adjustment.batch.processor.CreateAggregationProcessor;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.adjustment.repository.AdjustmentRepository;
import com.sparta.adjustment.domain.video.DayVideoLog;
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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class DayAggregationSingleThreadBatch extends DefaultBatchConfiguration {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerFactory entityManagerFactory;
    private final AdjustmentRepository adjustmentRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private int chunkSize;

    @Value("${chunkSize:1000}")
    public void setChunkSize(int chunkSize){
        this.chunkSize = chunkSize;
    }

    @Bean
    public Job aggregationProcessingJob2(JobRepository jobRepository,
                                 Step processHistoryForAggregation2,
                                 Step updateAdjustDataStep2){
        return new JobBuilder("aggregationProcessingJob2", jobRepository)
                .start(processHistoryForAggregation2)
                .next(updateAdjustDataStep2)
                .listener(new MyJobExecutionListener())
                .build();
    }

    //step1
    @Bean
    public Step processHistoryForAggregation2(JobRepository jobRepository){
        return new StepBuilder("step1", jobRepository)
                .<DayVideoLog, Aggregation> chunk(chunkSize, platformTransactionManager)
                .reader(dayVideoLogItemReader2(null))
                .processor(aggregationItemProcessor2(null))
                .writer(aggregationDataWriter2())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DayVideoLog> dayVideoLogItemReader2(
            @Value("#{jobParameters[referenceDate]}") String referenceDate){

        LocalDateTime start = LocalDate.parse(referenceDate).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);

        return new JpaPagingItemReaderBuilder<DayVideoLog>()
                .name("dayVideoLogItemReader")
                .pageSize(500)
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT d " +
                        "FROM DayVideoLog d " +
                        "WHERE d.createdAt >= :start " +
                        "AND d.createdAt < :end ")
                .parameterValues(params)
                .build();

    }

    @Bean
    @StepScope
    public ItemProcessor<DayVideoLog, Aggregation> aggregationItemProcessor2(
            @Value("#{jobParameters[referenceDate]}") String referenceDate){
        return new CreateAggregationProcessor(referenceDate, jdbcTemplate, adjustmentRepository);
    }
    @Bean
    public JdbcBatchItemWriter<Aggregation> aggregationDataWriter2(){
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
    public Step updateAdjustDataStep2(JobRepository jobRepository){
        return new StepBuilder("step2", jobRepository)
                .<Aggregation, Aggregation> chunk(chunkSize, platformTransactionManager)
                .reader(aggregationJpaItemReader2(null))
                .writer(adjustmentDataWriter2())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(3)
                .skipPolicy(new DayAggregationSkipPolicy())
                .build();
    }
    @Bean
    @StepScope
    public JpaPagingItemReader<Aggregation> aggregationJpaItemReader2(
            @Value("#{jobParameters[currentDate]}") String currentDate){

        LocalDateTime start = LocalDate.parse(currentDate).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Map<String, Object> params = new HashMap<>();

        params.put("start", start);
        params.put("end", end);

        return new JpaPagingItemReaderBuilder<Aggregation>()
                .name("aggregationJpaReader")
                .pageSize(500)
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a " +
                        "FROM Aggregation a " +
                        "WHERE a.createdAt >= :start " +
                        "AND a.createdAt < :end")
                .parameterValues(params)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Aggregation> adjustmentDataWriter2(){
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

}
