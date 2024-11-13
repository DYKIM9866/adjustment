package com.sparta.adjustment.batch.partitioner;

import com.sparta.adjustment.domain.adjustment.repository.AdjustmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CheckHistoryForCreateAggregationPartitioner implements Partitioner {

    private final AdjustmentRepository adjustmentRepository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long min = adjustmentRepository.findMinId();
        long max = adjustmentRepository.findMaxId();

        long targetSize = (max-min) / gridSize + 1;

        //담기 시작
        Map<String, ExecutionContext> division = new HashMap<>();
        int number = 0; //배치 번호
        long start = min;
        long end = start + targetSize - 1;

        while(start <= max){
            ExecutionContext val = new ExecutionContext();
            division.put("partition" + number++, val);

            //끝 번호보다 작을 경우 끝 번호 갱신
            if(end >= max) end = max;

            val.putLong("minId", start);
            val.putLong("maxId", end);

            start += targetSize;
            end += targetSize;
        }

        return division;
    }
}
