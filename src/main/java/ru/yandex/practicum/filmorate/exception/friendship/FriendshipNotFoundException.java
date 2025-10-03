package ru.yandex.practicum.filmorate.exception.friendship;

import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

public class FriendshipNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public FriendshipNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("friendship", "Дружбы у id: " + id + " не найдено."));
    }
}
