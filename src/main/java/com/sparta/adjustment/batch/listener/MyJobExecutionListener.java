package com.sparta.adjustment.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

@Slf4j
public class MyJobExecutionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info(jobExecution.getJobInstance().getJobName() + " Start!");
    }

    @AfterJob
    public void afterJob(JobExecution jobexecution) {
        log.info(jobexecution.getJobInstance().getJobName() + " " + jobexecution.getExitStatus());
        log.info(jobexecution.getJobInstance().getJobName() + " " + jobexecution.getStatus());
    }
}
