package ru.yandex.practicum.filmorate.repository.film_genre;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreRepository {
    List<FilmGenre> findGenresByFilmId(Long filmId);
}
