package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class FilmDurationValidator implements ConstraintValidator<FilmDurationAnnotation, Duration> {
    @Override
    public void initialize(FilmDurationAnnotation annotation) {
    }

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext context) {
        if (duration == null) {
            return true;
        }

        return !duration.isNegative() && !duration.isZero();
    }
}
