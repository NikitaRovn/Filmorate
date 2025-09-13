package ru.yandex.practicum.filmorate.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.dto.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse handleUserNotFoundException(UserNotFoundException e, HttpServletRequest r) {
        List<ErrorResponse> errors = e.getErrors().stream().toList();

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Пользователь не найден.",
                r.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse handleFilmNotFoundException(FilmNotFoundException e, HttpServletRequest r) {
        List<ErrorResponse> errors = e.getErrors().stream().toList();

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Фильм не найден.",
                r.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(FriendNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse handleFriendNotFoundException(FriendNotFoundException e, HttpServletRequest r) {
        List<ErrorResponse> errors = e.getErrors().stream().toList();

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Друг не найден.",
                r.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidation(MethodArgumentNotValidException e, HttpServletRequest r) {
        e.getBindingResult().getFieldErrors().forEach(error ->
                log.warn("Поле: '{}'. Ошибка: {}",
                        error.getField(),
                        error.getDefaultMessage()));

        List<ErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorResponse(f.getField(), f.getDefaultMessage()))
                .toList();

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Ошибка валидации данных.",
                r.getRequestURI(),
                errors
        );
    }
}
