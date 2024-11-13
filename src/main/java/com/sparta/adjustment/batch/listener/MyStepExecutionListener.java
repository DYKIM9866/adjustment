package com.sparta.adjustment.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class MyStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info(stepExecution.getStepName() + " Started!");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info(stepExecution.getStepName() + " " + stepExecution.getExitStatus());
        log.info(stepExecution.getStepName() + " batchStatus = " + stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
