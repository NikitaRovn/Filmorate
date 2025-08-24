package ru.yandex.practicum.filmorate.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDateAnnotation, LocalDate> {
    private final LocalDate restrictionDate = LocalDate.of(1985, 12, 28);

    @Override
    public void initialize(FilmReleaseDateAnnotation annotation) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            return true;
        }

        return !releaseDate.isBefore(restrictionDate);
    }
}
