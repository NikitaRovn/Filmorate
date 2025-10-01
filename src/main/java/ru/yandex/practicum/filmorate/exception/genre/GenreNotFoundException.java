package ru.yandex.practicum.filmorate.exception.genre;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class GenreNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public GenreNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("genre", "Жанр с id: " + id + " не существует."));
    }
}
