package ru.yandex.practicum.filmorate.exception.user;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public UserNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("user", "Пользователь с id: " + id + " не существует."));
    }
}
