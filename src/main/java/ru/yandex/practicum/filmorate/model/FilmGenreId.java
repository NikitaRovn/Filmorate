package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class FilmGenreId {
    private Long filmId;
    private Long genreId;
}
