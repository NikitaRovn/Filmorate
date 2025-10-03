package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.EmbeddedId;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FilmGenre {
    @EmbeddedId
    private FilmGenreId id;

    Film film;
    Genre genre;
    Integer sortOrder;
}
