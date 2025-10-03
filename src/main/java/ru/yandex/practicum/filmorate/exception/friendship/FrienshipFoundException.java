package ru.yandex.practicum.filmorate.exception.friendship;

import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

public class FrienshipFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public FrienshipFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("friendship", "Дружбы с id: " + id + " уже существует."));
    }
}
