package com.sparta.adjustment.batch.partitioner;

import com.sparta.adjustment.domain.video.repository.DayVideoLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CheckHistoryForCreateAggregationPartitioner implements Partitioner {

    private final DayVideoLogRepository dayVideoLogRepository;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<Long> logIds = dayVideoLogRepository.getLogIds(startTime, endTime);

        long size = logIds.size();
        if(size == 0) return Collections.emptyMap();
        long scope = (size+gridSize-1) / gridSize;

        //담기 시작
        Map<String, ExecutionContext> division = new HashMap<>();
        int number = 0; //배치 번호

        int startIdx = 0;

        while(startIdx < size){
            int endIdx = (int) Math.min(size-1, startIdx + scope -1);

            ExecutionContext val = new ExecutionContext();
            val.putLong("minId", logIds.get(startIdx));
            val.putLong("maxId", logIds.get(endIdx));

            division.put("partition" + number++, val);

            startIdx = endIdx + 1;
        }

        return division;
    }
}
