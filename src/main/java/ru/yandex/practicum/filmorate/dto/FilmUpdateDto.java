package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FilmUpdateDto {
    @NotNull(message = "Поле name должно быть передано.")
    @NotBlank(message = "Поле name не должно быть пустой строкой и строкой из пробелов.")
    String name;

    @Size(max = 200, message = "Поле description не должно быть больше 200 символов.")
    String description;

    @NotNull(message = "Поле releaseDate должно быть передано.")
    LocalDate releaseDate;

    @NotNull(message = "Поле duration должно быть передано.")
    @Positive(message = "Длительность фильма должна быть больше нуля.")
    Integer duration;

    @NotNull
    Long mpaRatingId;

    List<Long> genresIds;
}
