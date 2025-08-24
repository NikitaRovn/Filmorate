package ru.yandex.practicum.filmorate.exception.film;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

import java.util.List;

@Getter
public class FilmNotFoundException extends RuntimeException {
    private final List<ErrorResponse> errors;

    public FilmNotFoundException(Long id) {
        this.errors = List.of(new ErrorResponse("film", "Фильм с id: " + id + " не существует."));
    }
}
