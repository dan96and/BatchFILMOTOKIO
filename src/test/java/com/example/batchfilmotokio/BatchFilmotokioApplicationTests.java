package com.example.batchfilmotokio;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@RequiredArgsConstructor
class BatchFilmotokioApplicationTests {

	@Autowired
	 JobLauncher jobLauncher;
	@Autowired
	 Job proccesJob;

	@Test
	void executorTest() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("jobExecution", UUID.randomUUID().toString())
				.toJobParameters();

		jobLauncher.run(proccesJob, jobParameters);
	}
}
