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
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Список тестов:
// 1. Проверка, что возвращается список всех МПА рейтингов.
// 2. Проверка, что возвращается конкретный МПА рейтинг по его ID.
// 3. Проверка, что возвращается верная ошибка при вызове несуществующего ID.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MpaRatingControllerTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @DisplayName("1. Проверка, что возвращается список всех МПА рейтингов.")
    @Test
    void shouldReturnAllMpaRatings() {
        String baseUrl = "http://localhost:" + port + "/mpa";

        List<MpaRatingDto> ratings = List.of(restTemplate.getForObject(baseUrl, MpaRatingDto[].class));

        assertThat(ratings)
                .extracting(MpaRatingDto::getId, MpaRatingDto::getName)
                .containsExactly(
                        tuple(1L, "G"),
                        tuple(2L, "PG"),
                        tuple(3L, "PG-13"),
                        tuple(4L, "R"),
                        tuple(5L, "NC-17")
                );
    }

    @DisplayName("2. Проверка, что возвращается конкретный МПА рейтинг по его ID.")
    @Test
    void shouldReturnMpaRatingById() {
        String baseUrl = "http://localhost:" + port + "/mpa/{id}";

        MpaRatingDto rating = restTemplate.getForObject(baseUrl, MpaRatingDto.class, 3L);

        assertThat(rating)
                .extracting(MpaRatingDto::getId, MpaRatingDto::getName)
                .containsExactly(3L, "PG-13");
    }

    @DisplayName("3. Проверка, что возвращается верная ошибка при вызове несуществующего ID.")
    @Test
    void shouldReturnNotFoundForInvalidMpaRatingId() {
        String baseUrl = "http://localhost:" + port + "/mpa/{id}";

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(baseUrl, String.class, 999L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getResponseBodyAsString()).contains("МПА рейтинг не найден.");
    }
}
