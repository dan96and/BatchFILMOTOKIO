package com.example.batchfilmotokio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDTO {

    private Long id;

    private String title;

    private Integer year;

    private Integer duration;

    private String synopsis;
}
