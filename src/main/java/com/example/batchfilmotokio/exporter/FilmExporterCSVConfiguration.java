package com.example.batchfilmotokio.exporter;

import com.example.batchfilmotokio.domain.Film;
import com.example.batchfilmotokio.dto.FilmDTO;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Paths;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FilmExporterCSVConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FilmCSVExporterBatchListener listener;

    @Bean
    public JpaPagingItemReader<Film> reader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Film>()
                .name("filmReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT f FROM Film f WHERE f.migrate = false")
                .build();
    }

    @Bean
    public ItemProcessor<Film, FilmDTO> processor() {

        return film -> {
            FilmDTO filmDTO = new FilmDTO();

            filmDTO.setId(film.getId());
            filmDTO.setTitle(film.getTitle());
            filmDTO.setYear(film.getYear());
            filmDTO.setDuration((film.getDuration()));
            filmDTO.setSynopsis(film.getSynopsis());

            return filmDTO;
        };
    }

    @Bean
    public FlatFileItemWriter<FilmDTO> writer() {
        FlatFileItemWriter<FilmDTO> writer = new FlatFileItemWriter<>();

        writer.setResource(new FileSystemResource(Paths.get("src/main/resources/static/exports", "film.csv")));

        writer.setAppendAllowed(true);

        writer.setHeaderCallback(writerHeader -> writerHeader.write("id,title,year,duration,synopsis"));
        writer.setLineAggregator(new DelimitedLineAggregator<>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                setNames(new String[]{"id", "title", "year", "duration", "synopsis"});
            }});
        }});

        return writer;
    }

    @Bean
    public Step step(ItemReader<Film> reader, ItemProcessor<Film, FilmDTO> processor, ItemWriter<FilmDTO> writer) {
        return new StepBuilder("step", jobRepository)
                .<Film, FilmDTO>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job movieMigrationJob(Step step) {
        return new JobBuilder("filmMigrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .listener(listener)
                .build();
    }

}