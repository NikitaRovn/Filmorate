package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserFilmLikeId implements Serializable {
    private Long userId;
    private Long filmId;
}
