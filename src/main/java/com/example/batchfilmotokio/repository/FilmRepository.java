package com.example.batchfilmotokio.repository;

import com.example.batchfilmotokio.domain.Film;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface FilmRepository extends JpaRepository<Film, Long> {

    int countAllByMigrateFalse();

    @Transactional
    @Modifying
    @Query(value = "UPDATE Film f SET f.migrate = true, f.dateMigrate =:dateToday WHERE f.migrate = false")
    void updateFilmsMigrates(@Param("dateToday") LocalDate date);
}
