package com.sparta.adjustment.batch;

import com.sparta.adjustment.domain.adjustment.Adjustment;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import com.sparta.adjustment.domain.video.Video;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DayAggregationBatchProcessorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DayAggregationBatch dayAggregationBatch;

    @Test
    @DisplayName("Processor가 조회수, 광고, 시청시간, 정산 금액을 잘 저장 하는지 확인 ")
    void testProcessor() throws Exception {
        //given
        Adjustment adjustment = new Adjustment();
        adjustment.setId(1L);
        adjustment.setTotalViews(499_999L);
        adjustment.setTotalAdViews(999_998L);
        List<UserVideoCheckHistory> mockHistories = Arrays.asList(
                new UserVideoCheckHistory(1L, 1L, 1, 30),
                new UserVideoCheckHistory(1L, 1L, 2, 25)
        );

        //when
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(mockHistories);
        Aggregation result = dayAggregationBatch.processor(jdbcTemplate).process(adjustment);

        //then
        assertEquals(2, result.getViews());
        assertEquals(3, result.getAdViews());
        assertEquals(55, result.getViewingTime());
        assertEquals(2, result.getViewsAmount());
        assertEquals(55, result.getAdAmount());
    }
}