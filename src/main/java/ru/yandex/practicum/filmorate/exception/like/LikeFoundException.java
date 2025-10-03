package ru.yandex.practicum.filmorate.exception.like;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class LikeFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public LikeFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("genre", "Лайк фильму юзера с id: " + id + " уже существует."));
    }
}
