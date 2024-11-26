package com.sparta.adjustment.batch.partitioner;

import com.sparta.adjustment.domain.video.repository.DayVideoLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckHistoryForCreateAggregationPartitionerTest {

    @Mock
    private DayVideoLogRepository dayVideoLogRepository;

    private final String referenceDate = "2024-11-25";

    @InjectMocks
    private CheckHistoryForCreateAggregationPartitioner partitioner;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(partitioner, "referenceDate", "2024-11-25");
    }

    @Test
    @DisplayName("잘 파티셔닝 하는지 확인")
    void partitioning() {
        //given
        int cnt = 58;
        // 1L ~ 100L 사이의 숫자 중 무작위로 50개 선택
        List<Long> randomList = new Random()
                .longs(1L, 101L)
                .distinct()
                .limit(cnt)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        //10개 출력 후 개행
        for (int i = 0; i < randomList.size(); i++) {
            System.out.print(randomList.get(i) + " ");
            if((i+1)% 10 == 0) System.out.println();
        }
        System.out.println();

        when(dayVideoLogRepository.getLogIds(referenceDate))
                .thenReturn(randomList);
        int gridSize = 5;

        //when
        Map<String, ExecutionContext> partitions = partitioner.partition(gridSize);

        //then
        int size = randomList.size();
        long scope = (size+gridSize-1) / gridSize;
        List<int[]> l = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < 5; i++) {
            int end = (int) Math.min(size-1, start + scope -1);
            l.add(new int[]{start, end});
            start += (int)scope;
        }

        for (int i = 0; i < 5; i++) {
            String key = "partition" + i;
            ExecutionContext executionContext = partitions.get(key);
            long minId = executionContext.getLong("minId");
            long maxId = executionContext.getLong("maxId");
            int[] expected = l.get(i);
            assertEquals(randomList.get(expected[0]), minId);
            assertEquals(randomList.get(expected[1]), maxId);
        }

    }

}