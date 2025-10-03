package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRatingDto mpaRating;
    private List<GenreDto> genres;
}
