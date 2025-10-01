package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserRegisterDto {

    @NotNull(message = "Поле email должно быть передано.")
    @NotBlank(message = "Поле email не должно быть пустой строкой или строкой из пробелов.")
    @Email(message = "Поле email должно быть в корректном формате.")
    String email;

    @NotNull(message = "Поле login должно быть передано.")
    @NotBlank(message = "Поле login не должно быть пустой строкой или строкой из пробелов.")
    @Pattern(regexp = "^\\S+$", message = "Поле login не должно содержать пробелов, табуляций и переносов строк.")
    String login;

    String name;

    @NotNull(message = "Поле birthday должно быть передано.")
    @PastOrPresent(message = "Поле birthday должно быть не позже текущей даты.")
    LocalDate birthday;
}
