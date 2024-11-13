package com.sparta.adjustment.batch.partitioner;

import com.sparta.adjustment.domain.adjustment.repository.AggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CheckAggregationForUpdateAdjustmentPartitioner implements Partitioner {

    private final AggregationRepository aggregationRepository;
    private final String currentDate;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        LocalDate localDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime startTime = LocalDateTime.of(localDate, LocalTime.of(0,0,0));
        LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.of(23,59,59));

        long min = aggregationRepository.getMinId(startTime, endTime);
        long max = aggregationRepository.getMaxId(startTime, endTime);

        long targetSize = (max - min) / gridSize - 1;

        //담기 시작
        Map<String, ExecutionContext> division = new HashMap<>();
        int number = 0;
        long start = min;
        long end = start + targetSize - 1;

        while(start <= max){
            ExecutionContext val = new ExecutionContext();
            division.put("partition" + number++, val);

            if(end >= max) end = max;

            val.putLong("minId", start);
            val.putLong("maxId", end);

            start += targetSize;
            end += targetSize;
        }

        return division;
    }
}
