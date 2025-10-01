package ru.yandex.practicum.filmorate.exception.mpa_rating;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class MpaRatingNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public MpaRatingNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("mpa_rating", "МПА рейтинг с id: " + id + " не существует."));
    }
}
