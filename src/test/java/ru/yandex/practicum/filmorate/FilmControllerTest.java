package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Список тестов:
// 1. Проверка, что добавляется фильм.
// 2. Проверка, что не добавляет фильм с пустым названием.
// 3. Проверка, что не добавляет фильм с описанием длиннее 200 символов.
// 4. Проверка, что не добавляет фильм с датой выхода раньше 28.12.1895.
// 5. Проверка, что не добавляет фильм с нулевой или отрицательной длительностью.
// 6. Проверка, что сервер отдает список фильмов корректно.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private FilmRepository filmRepository;

    @AfterEach
    void tearDown() {
        filmRepository.clear();
    }

    @DisplayName("1. Проверка, что добавляется фильм.")
    @Test
    void shouldAddFilm() {
        Film film = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        ResponseEntity<Film> response = testRestTemplate.postForEntity(
                "/films",
                film,
                Film.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Film savedFilm = response.getBody();
        assertThat(savedFilm)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(film);

        assertThat(filmRepository.findAll()).hasSize(1);
    }

    @DisplayName("2. Проверка, что не добавляет фильм с пустым названием.")
    @Test
    void shouldRejectFilmIfNameEmpty() {
        Film film = Film.builder()
                .name("")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        ResponseEntity<Film> response = testRestTemplate.postForEntity("/films", film, Film.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("3. Проверка, что не добавляет фильм с описанием длиннее 200 символов.")
    @Test
    void shouldRejectFilmIfDescriptionTooLong() {
        String longDescription = "x".repeat(201);
        Film film = Film.builder()
                .name("Film1")
                .description(longDescription)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        ResponseEntity<Film> response = testRestTemplate.postForEntity("/films", film, Film.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("4. Проверка, что не добавляет фильм с датой выхода раньше 28.12.1895.")
    @Test
    void shouldRejectFilmIfReleaseDateTooEarly() {
        Film film = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1800, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        ResponseEntity<Film> response = testRestTemplate.postForEntity("/films", film, Film.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("5. Проверка, что не добавляет фильм с нулевой или отрицательной длительностью.")
    @Test
    void shouldRejectFilmIfDurationInvalid() {
        Film filmZero = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ZERO)
                .build();

        ResponseEntity<Film> response = testRestTemplate.postForEntity("/films", filmZero, Film.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Film filmNegative = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(-10))
                .build();

        response = testRestTemplate.postForEntity("/films", filmNegative, Film.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("6. Проверка, что сервер отдает список фильмов корректно.")
    @Test
    void shouldGetAllFilms() {
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        testRestTemplate.postForEntity("/films", film1, Film.class);

        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(2005, 5, 5))
                .duration(Duration.ofMinutes(90))
                .build();

        testRestTemplate.postForEntity("/films", film2, Film.class);

        ResponseEntity<Film[]> response = testRestTemplate.getForEntity("/films", Film[].class);
        Film[] films = response.getBody();

        assertThat(films).hasSize(2);
    }
}
