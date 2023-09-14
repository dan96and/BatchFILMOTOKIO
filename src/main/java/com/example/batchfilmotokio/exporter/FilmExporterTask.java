package com.example.batchfilmotokio.exporter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class FilmExporterTask {
    private final JobLauncher jobLauncher;
    private final Job exportAirportRawJob;

    @Scheduled(cron = "0 05 21 * * ?")
    public void launchAirportExporterCSV() {

        JobParameters jobParameters = new JobParametersBuilder().addString("jobExecution", UUID.randomUUID().toString()).toJobParameters();

        try {
            jobLauncher.run(exportAirportRawJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
