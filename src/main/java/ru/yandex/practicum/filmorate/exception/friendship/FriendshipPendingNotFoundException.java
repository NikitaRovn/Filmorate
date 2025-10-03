package ru.yandex.practicum.filmorate.exception.friendship;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class FriendshipPendingNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public FriendshipPendingNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("friendship", "Запроса дружбы у id: " + id + " не найдено."));
    }
}
