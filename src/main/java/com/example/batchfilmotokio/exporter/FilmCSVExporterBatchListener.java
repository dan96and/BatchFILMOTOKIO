package com.example.batchfilmotokio.exporter;

import com.example.batchfilmotokio.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmCSVExporterBatchListener implements JobExecutionListener {

    private final FilmRepository filmRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting job: {} Records to be migrated: {}",
                jobExecution.getJobInstance().getJobName(),
                filmRepository.countAllByMigrateFalse());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Ending job: {} status: {} error: {}",
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getStatus(),
                    jobExecution.getExitStatus().getExitDescription());
        } else {
            filmRepository.updateFilmsMigrates(LocalDate.now());
            log.info("Ending job: {} status: {} seconds:{} Records to be migrated: {}",
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getStatus(),
                    ChronoUnit.SECONDS.between(jobExecution.getStartTime(), jobExecution.getEndTime()),
                    filmRepository.countAllByMigrateFalse());
        }
    }
}
