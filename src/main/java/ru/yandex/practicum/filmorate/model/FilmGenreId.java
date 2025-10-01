package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class FilmGenreId {
    private Long userId;
    private Long filmId;
}
