package com.sparta.adjustment.batch.faultTolerant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

@Slf4j
public class DayAggregationSkipPolicy implements SkipPolicy {

    private int skipCount = 0;

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
        if(skipCount > 10){
            log.error("skip item 10개 초과");
            return false;
        }

        log.warn("item 처리 중 예외 발생: {}", t.getMessage());
        this.skipCount++;

        return true;
    }
}
