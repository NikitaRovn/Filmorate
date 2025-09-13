package ru.yandex.practicum.filmorate.exception.friend;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class FriendNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public FriendNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("friend", "Друг с id: " + id + " не в списке друзей."));
    }
}
