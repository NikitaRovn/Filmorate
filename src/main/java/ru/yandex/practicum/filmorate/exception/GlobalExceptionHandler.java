package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.dto.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        List<ErrorResponse> errors = e.getErrors().stream().toList();

        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationErrorResponse handleFilmNotFoundException(FilmNotFoundException e) {
        List<ErrorResponse> errors = e.getErrors().stream().toList();

        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidation(MethodArgumentNotValidException e) {
        e.getBindingResult().getFieldErrors().forEach(error ->
                log.warn("Провал валидации поля '{}'. Ошибка: {}",
                        error.getField(),
                        error.getDefaultMessage()));

        List<ErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorResponse(f.getField(), f.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ValidationErrorResponse(errors);
    }
}
