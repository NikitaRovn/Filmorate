package ru.yandex.practicum.filmorate.exception.like;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class LikeNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public LikeNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("like", "Лайк фильму юзера с id: " + id + " не существует."));
    }
}
