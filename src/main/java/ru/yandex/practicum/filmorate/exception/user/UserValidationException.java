package ru.yandex.practicum.filmorate.exception.user;

public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}