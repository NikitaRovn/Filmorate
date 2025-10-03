package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDateAnnotation;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FilmRegisterDto {
    @NotNull(message = "Поле name должно быть передано.")
    @NotBlank(message = "Поле name не должно быть пустой строкой и строкой из пробелов.")
    String name;

    @Size(max = 200, message = "Поле description не должно быть больше 200 символов.")
    String description;

    @NotNull(message = "Поле releaseDate должно быть передано.")
    @FilmReleaseDateAnnotation(message = "Дата выхода фильма не может быть раньше дня рождения кино (28.12.1985).")
    LocalDate releaseDate;

    @NotNull(message = "Поле duration должно быть передано.")
    @Positive(message = "Длительность фильма должна быть больше нуля.")
    Integer duration;

    @NotNull(message = "Поле mpaRatingId должно быть передано.")
    Long mpaRatingId;

    @NotNull(message = "Список genresIds должно быть передано.")
    @NotEmpty(message = "Список genresIds должен быть не пустой.")
    List<Long> genresIds;
}
