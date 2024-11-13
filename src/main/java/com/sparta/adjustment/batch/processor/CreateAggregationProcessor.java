package com.sparta.adjustment.batch.processor;

import com.sparta.adjustment.domain.adjustment.Adjustment;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
public class CreateAggregationProcessor implements ItemProcessor<Adjustment, Aggregation> {

    private static final float[] VIEWS_GRADE_PRICE = {1, 1.1f, 1.3f, 1.5f};
    private static final float[] AD_VIEWS_GRADE_PRICE = {10, 12, 15, 20};
    private static final int[] VIEWS_GRADE_LIMIT = {100_000, 500_000, 1_000_000, Integer.MAX_VALUE};

    private final String referenceDate;
    private final JdbcTemplate jdbcTemplate;

    public CreateAggregationProcessor(String referenceDate, JdbcTemplate jdbcTemplate) {
        this.referenceDate = referenceDate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Aggregation process(Adjustment item) throws Exception {

        Long videoId = item.getId();

        List<UserVideoCheckHistory> histories = jdbcTemplate.query(
                "SELECT * FROM user_video_check_history where video_id = ? " +
                        "and to_char(created_at,'YYYY-MM-DD') = ?",
                new Object[]{videoId, referenceDate},
                new BeanPropertyRowMapper<>(UserVideoCheckHistory.class)
        );

        if(histories.isEmpty()) return null;

        long views = 0;
        long adViews = 0;
        long viewingTime = 0;
        for(UserVideoCheckHistory history : histories){
            views++;
            adViews += history.getAdViews();
            viewingTime += history.getViewingTime();
        }

        long viewsPrice = calViews(item.getTotalViews(), views);
        long adViewsPrice = calAd(item.getTotalAdViews(), adViews);


        return  new Aggregation().builder()
                .videoId(videoId)
                .viewsAmount(viewsPrice)
                .adAmount(adViewsPrice)
                .views(views)
                .adViews(adViews)
                .viewingTime(viewingTime)
                .build();
    }

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
