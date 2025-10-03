package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.Film;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpaRating(MpaRatingDto.builder()
                        .id(film.getMpaRating().getId())
                        .name(film.getMpaRating().getName())
                        .build())
                .genres(film.getGenres().stream()
                        .map(g -> GenreDto.builder()
                                .id(g.getId())
                                .name(g.getName())
                                .build())
                        .toList())
                .build();
    }
}
