package com.sparta.adjustment.batch;

import com.sparta.adjustment.domain.adjustment.Adjustment;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@Slf4j
@Configuration
public class DayAggregationBatch extends DefaultBatchConfiguration {

    private static final LocalDate CURRENT_DATE = LocalDate.now();

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(super.getDataSource());
    }

    @Bean
    public Job dayAggregationJob(JobRepository jobRepository,
                                 Step step1,
                                 Step step2){
        return new JobBuilder("dayAggregationJob", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      JdbcPagingItemReader<Adjustment> adjustmentReader,
                      ItemProcessor<Adjustment, Aggregation> processor,
                      JdbcBatchItemWriter<Aggregation> writer){
        log.debug("DayAggregationBatch step1 start");
        return new StepBuilder("step1", jobRepository)
                .<Adjustment, Aggregation> chunk(100, getTransactionManager())
                .reader(adjustmentReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository,
                      JdbcPagingItemReader<Aggregation> aggregationReader,
                      JdbcBatchItemWriter<Aggregation> adjustmentWriter){
        return new StepBuilder("step2", jobRepository)
                .<Aggregation, Aggregation> chunk(100, getTransactionManager())
                .reader(aggregationReader)
                .writer(adjustmentWriter)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Aggregation> aggregationReader(DataSource dataSource){
        return new JdbcPagingItemReaderBuilder<Aggregation>()
                .name("aggregationReader")
                .dataSource(dataSource)
                .pageSize(100)
                .selectClause("SELECT *")
                .fromClause("FROM aggregation a")
                .whereClause("WHERE to_char(a.created_at,'YYYY-MM-DD') = :created_at")
                .parameterValues(Collections.singletonMap("created_at", CURRENT_DATE.toString()))
                .sortKeys(Collections.singletonMap("id", Order.ASCENDING))
                .rowMapper(new BeanPropertyRowMapper<>(Aggregation.class))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Aggregation> adjustmentWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Aggregation>()
                .dataSource(dataSource)
                .sql("UPDATE adjustment " +
                        "SET " +
                        "    total_views = total_views + :views, " +
                        "    total_ad_views = total_ad_views + :adViews, " +
                        "    total_play_time = total_play_time + :viewingTime, " +
                        "    total_amount = total_amount + :viewsAmount + :adAmount " +
                        "WHERE id = :videoId")
                .beanMapped().build();
    }

    @Bean
    public JdbcPagingItemReader<Adjustment> adjustmentReader(DataSource dataSource){
        log.debug("dayAggregationJob-step1-adjustmentReader start");
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
    public ItemProcessor<Adjustment, Aggregation> processor(JdbcTemplate jdbcTemplate){
        log.debug("dayAggregationJob-step1-processor start");
        return adjustment ->{
            //여기서 history 전부 조회해서 값 계산 후 Aggregation 만들어서 넘겨줌
            Long videoId =  adjustment.getId();

            List<UserVideoCheckHistory> histories = jdbcTemplate.query(
                    "SELECT * FROM user_video_check_history where video_id = ? " +
                            "and to_char(created_at,'YYYY-MM-DD') = ?",
                    new Object[]{videoId, CURRENT_DATE.minusDays(1).toString()},
                    new BeanPropertyRowMapper<>(UserVideoCheckHistory.class)
            );

            long views = 0;
            long adViews = 0;
            long viewingTime = 0;
            for(UserVideoCheckHistory history : histories){
                views++;
                adViews += history.getAdViews();
                viewingTime += history.getViewingTime();
            }

            long viewsPrice = calViews(adjustment.getTotalViews(), views);
            long adViewsPrice = calAd(adjustment.getTotalAdViews(), adViews);

            return  new Aggregation().builder()
                    .videoId(videoId)
                    .viewsAmount(viewsPrice)
                    .adAmount(adViewsPrice)
                    .views(views)
                    .adViews(adViews)
                    .viewingTime(viewingTime)
                    .build();
        };
    }

    @Bean
    public JdbcBatchItemWriter<Aggregation> writer(DataSource dataSource){
        log.debug("dayAggregationJob-step1-writer start");
        return new JdbcBatchItemWriterBuilder<Aggregation>()
                .dataSource(dataSource)
                .sql("Insert into aggregation(video_id, views_amount, ad_amount, views, ad_views, viewing_time, created_at)" +
                                "values (:videoId, :viewsAmount, :adAmount, :views, :adViews, :viewingTime, now())")
                        .beanMapped().build();
    }

    private static final float[] VIEWS_GRADE_PRICE = {1, 1.1f, 1.3f, 1.5f};
    private static final float[] AD_VIEWS_GRADE_PRICE = {10, 12, 15, 20};
    private static final int[] VIEWS_GRADE_LIMIT = {100_000, 500_000, 1_000_000, Integer.MAX_VALUE};

    private long calViews(Long totalViews, long views) {
        long price = 0;
        int grade = getViewsGradeLimit(totalViews);

        if(grade == 3) return (long) (views * VIEWS_GRADE_PRICE[3]);

        long maxViewsAtGrade = VIEWS_GRADE_LIMIT[grade] - totalViews - 1;
        for(; views > 0 && grade < VIEWS_GRADE_PRICE.length; grade++){
            if(views > maxViewsAtGrade){
                price += (long)(maxViewsAtGrade * VIEWS_GRADE_PRICE[grade]);
                views -= maxViewsAtGrade;
                maxViewsAtGrade = VIEWS_GRADE_LIMIT[grade+1] - 1;
            }else{
                price += (long)(views * VIEWS_GRADE_PRICE[grade]);
                break;
            }
        }

        return price;
    }

    private long calAd(Long totalAdViews, long adViews) {
        long price = 0;
        int grade = getViewsGradeLimit(totalAdViews);

        if(grade == 3) return (long) (adViews * AD_VIEWS_GRADE_PRICE[3]);

        long maxViewsAtGrade = VIEWS_GRADE_LIMIT[grade] - totalAdViews - 1;
        for(; adViews > 0 && grade < AD_VIEWS_GRADE_PRICE.length; grade++){
            if(adViews > maxViewsAtGrade){
                price += (long)(maxViewsAtGrade * AD_VIEWS_GRADE_PRICE[grade]);
                adViews -= maxViewsAtGrade;
                maxViewsAtGrade = VIEWS_GRADE_LIMIT[grade+1] - 1;
            }else{
                price += (long)(adViews * AD_VIEWS_GRADE_PRICE[grade]);
                break;
            }
        }

        return price;
    }

    private int getViewsGradeLimit(Long totalViews) {
        for (int i = 0; i < VIEWS_GRADE_LIMIT.length; i++) {
            if(totalViews <= VIEWS_GRADE_LIMIT[i]) return i;
        }
        return VIEWS_GRADE_LIMIT.length - 1;
    }
}
