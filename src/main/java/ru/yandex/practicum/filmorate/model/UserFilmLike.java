package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.EmbeddedId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFilmLike {
    @EmbeddedId
    private UserFilmLikeId id;

    Film film;
    User user;
}
