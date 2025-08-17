package ru.yandex.practicum.filmorate.exception.film;

public class FilmValidationException extends RuntimeException {
    public FilmValidationException(String message) {
        super(message);
    }
}