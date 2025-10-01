package ru.yandex.practicum.filmorate.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Список тестов:
// 1. Проверка, что возвращается список всех жанров.
// 2. Проверка, что возвращается конкретный жанр по его ID.
// 3. Проверка, что возвращается верная ошибка при вызове несуществующего ID.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenreControllerTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @DisplayName("1. Проверка, что возвращается список всех жанров.")
    @Test
    void shouldReturnAllGenres() {
        String baseUrl = "http://localhost:" + port + "/genres";

        List<GenreDto> genres = List.of(restTemplate.getForObject(baseUrl, GenreDto[].class));

        assertThat(genres)
                .extracting(GenreDto::getId, GenreDto::getName)
                .containsExactly(
                        tuple(1L, "Комедия"),
                        tuple(2L, "Драма"),
                        tuple(3L, "Мультфильм"),
                        tuple(4L, "Триллер"),
                        tuple(5L, "Документальный"),
                        tuple(6L, "Боевик"),
                        tuple(7L, "Ужасы"),
                        tuple(8L, "Фантастика"),
                        tuple(9L, "Фэнтези")
                );
    }

    @DisplayName("2. Проверка, что возвращается конкретный жанр по его ID.")
    @Test
    void shouldReturnGenreById() {
        String baseUrl = "http://localhost:" + port + "/genres/{id}";

        GenreDto genre = restTemplate.getForObject(baseUrl, GenreDto.class, 3L);

        assertThat(genre)
                .extracting(GenreDto::getId, GenreDto::getName)
                .containsExactly(3L, "Мультфильм");
    }

    @DisplayName("3. Проверка, что возвращается верная ошибка при вызове несуществующего ID.")
    @Test
    void shouldReturnNotFoundForInvalidGenreId() {
        String baseUrl = "http://localhost:" + port + "/genres/{id}";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(baseUrl, String.class, 999L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getResponseBodyAsString()).contains("Жанр не найден");
    }
}
