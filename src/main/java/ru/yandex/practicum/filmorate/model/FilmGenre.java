package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.EmbeddedId;

public class FilmGenre {
    @EmbeddedId
    private FilmGenreId id;

    Film film;
    Genre genre;
    Integer sortOrder;
}
