package com.sparta.adjustment.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DayAggregationBatchScheduler {

    private final JobLauncher jobLauncher;
    private Job dayAggregationJob;

    @Scheduled(cron = "")
    public void runDayAggregationAdjustment(){
        try {
            jobLauncher.run(dayAggregationJob,
                    new JobParametersBuilder()
                            .addLong("startAt", System.currentTimeMillis())
                            .addString("referenceDate", LocalDate.now().minusDays(2).toString())
                            .addString("currentDate", LocalDate.now().toString())
                            .toJobParameters());
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
