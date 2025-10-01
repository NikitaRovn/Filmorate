package ru.yandex.practicum.filmorate.integration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmRegisterDto;
import ru.yandex.practicum.filmorate.dto.UserRegisterDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

// Список тестов:
// 1. Проверка, что добавляется фильм.
// 2. Проверка, что не добавляет фильм с пустым названием.
// 3. Проверка, что не добавляет фильм с описанием длиннее 200 символов.
// 4. Проверка, что не добавляет фильм с описанием ровно 200 символов.
// 5. Проверка, что не добавляет фильм с датой выхода раньше 28.12.1895.
// 6. Проверка, что не добавляет фильм с нулевой длительностью.
// 7. Проверка, что не добавляет фильм с отрицательной длительностью.
// 8. Проверка, что сервер отдает список фильмов корректно.
// 9. Проверка, что пользователь может ставить лайк фильму.
// 10. Проверка, что пользователь может удалять лайк.
// 11. Проверка, что выводится список фильмов по лайкам.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @DisplayName("1. Проверка, что добавляется фильм.")
    @Test
    void shouldAddFilm() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Имя тестового фильма 1.")
                .description("Описание тестового фильма 1.")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpaRatingId(3L)
                .genresIds(List.of(2L, 4L, 5L))
                .build();

        var response = restTemplate.postForEntity("/films", dto, Film.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        Film savedFilm = response.getBody();
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo(dto.getName());
        assertThat(savedFilm.getDescription()).isEqualTo(dto.getDescription());
        assertThat(savedFilm.getDuration()).isEqualTo(dto.getDuration());

        assertThat(savedFilm.getGenres())
                .isNotNull()
                .hasSize(3)
                .extracting(Genre::getId)
                .containsAll(dto.getGenresIds());

        assertThat(savedFilm.getMpaRating()).isNotNull();
        assertThat(savedFilm.getMpaRating().getId()).isEqualTo(dto.getMpaRatingId());
    }

    @DisplayName("2. Проверка, что не добавляет фильм с пустым названием.")
    @Test
    void shouldNotAddFilmWithEmptyName() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("   ")
                .description("Описание тестового фильма 2.")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(1L, 2L))
                .build();

        var response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        String body = response.getBody();
        System.out.println("Ответ при ошибке валидации: " + body);
        assertThat(body).isNotNull().contains("name");
    }

    @DisplayName("3. Проверка, что не добавляет фильм с описанием длиннее 200 символов.")
    @Test
    void shouldNotAddFilmWithTooLongDescription() {
        String longDescription = "A".repeat(201);
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм с длинным описанием")
                .description(longDescription)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        String body = response.getBody();
        System.out.println("Ответ при ошибке валидации (длина > 200): " + body);
        assertThat(body).isNotNull().contains("description");
    }

    @DisplayName("4. Проверка, что добавляет фильм с описанием ровно 200 символов.")
    @Test
    void shouldAddFilmWithMaxDescriptionLength() {
        String exactDescription = "B".repeat(200);
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм с описанием 200 символов")
                .description(exactDescription)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", dto, Film.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Film savedFilm = response.getBody();
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getDescription()).isEqualTo(exactDescription);
    }

    @DisplayName("5. Проверка, что не добавляет фильм с датой выхода раньше 28.12.1895.")
    @Test
    void shouldNotAddFilmWithTooEarlyReleaseDate() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм с ранней датой")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1800, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        var response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        System.out.println("Ответ при ошибке валидации (слишком ранняя дата): " + response.getBody());
        assertThat(response.getBody()).contains("releaseDate");
    }

    @DisplayName("6. Проверка, что не добавляет фильм с нулевой длительностью.")
    @Test
    void shouldNotAddFilmWithZeroDuration() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм с нулевой длительностью")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(0)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        var response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        System.out.println("Ответ при ошибке валидации (нулевая длительность): " + response.getBody());
        assertThat(response.getBody()).contains("duration");
    }

    @DisplayName("7. Проверка, что не добавляет фильм с отрицательной длительностью.")
    @Test
    void shouldNotAddFilmWithNegativeDuration() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм с отрицательной длительностью")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(-100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        var response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        System.out.println("Ответ при ошибке валидации (отрицательная длительность): " + response.getBody());
        assertThat(response.getBody()).contains("duration");
    }

    @DisplayName("8. Проверка, что сервер отдает список фильмов корректно.")
    @Test
    void shouldReturnListOfFilms() {
        FilmRegisterDto dto1 = FilmRegisterDto.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L, 2L))
                .build();

        FilmRegisterDto dto2 = FilmRegisterDto.builder()
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(120)
                .mpaRatingId(2L)
                .genresIds(List.of(2L, 3L))
                .build();

        FilmRegisterDto dto3 = FilmRegisterDto.builder()
                .name("Фильм 3")
                .description("Описание фильма 3")
                .releaseDate(LocalDate.of(2022, 3, 3))
                .duration(140)
                .mpaRatingId(3L)
                .genresIds(List.of(1L, 3L))
                .build();

        restTemplate.postForEntity("/films", dto1, Film.class);
        restTemplate.postForEntity("/films", dto2, Film.class);
        restTemplate.postForEntity("/films", dto3, Film.class);

        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films", Film[].class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        Film[] films = response.getBody();
        assertThat(films).isNotNull();
        assertThat(films).hasSize(3);

        List<String> filmNames = List.of(films[0].getName(), films[1].getName(), films[2].getName());
        assertThat(filmNames).contains(dto1.getName(), dto2.getName(), dto3.getName());
    }

    @DisplayName("9. Проверка, что пользователь может ставить лайк фильму.")
    @Test
    void shouldAllowUserToLikeFilm() {
        UserRegisterDto userDto = UserRegisterDto.builder()
                .email("user@example.com")
                .login("userlogin")
                .name("User Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", userDto, User.class);
        assertThat(userResponse.getStatusCodeValue()).isEqualTo(200);
        User savedUser = userResponse.getBody();
        assertThat(savedUser).isNotNull();
        Long userId = savedUser.getId();

        FilmRegisterDto filmDto = FilmRegisterDto.builder()
                .name("Тестовый фильм для лайка")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<Film> filmResponse = restTemplate.postForEntity("/films", filmDto, Film.class);
        assertThat(filmResponse.getStatusCodeValue()).isEqualTo(200);
        Film savedFilm = filmResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        restTemplate.put("/films/{id}/like/{userId}", null, filmId, userId);

        ResponseEntity<Long[]> likesResponse = restTemplate.getForEntity("/films/{id}/likes", Long[].class, filmId);
        assertThat(likesResponse.getStatusCodeValue()).isEqualTo(200);
        Long[] likes = likesResponse.getBody();
        assertThat(likes).isNotNull().contains(userId);
    }

    @DisplayName("10. Проверка, что пользователь может удалять лайк фильму.")
    @Test
    void shouldAllowUserToRemoveLikeFromFilm() {

        UserRegisterDto userDto = UserRegisterDto.builder()
                .email("user2@example.com")
                .login("userlogin2")
                .name("User Name 2")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();

        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", userDto, User.class);
        assertThat(userResponse.getStatusCodeValue()).isEqualTo(200);
        User savedUser = userResponse.getBody();
        assertThat(savedUser).isNotNull();
        Long userId = savedUser.getId();

        FilmRegisterDto filmDto = FilmRegisterDto.builder()
                .name("Тестовый фильм для удаления лайка")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 2, 2))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<Film> filmResponse = restTemplate.postForEntity("/films", filmDto, Film.class);
        assertThat(filmResponse.getStatusCodeValue()).isEqualTo(200);
        Film savedFilm = filmResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        restTemplate.put("/films/{id}/like/{userId}", null, filmId, userId);

        restTemplate.delete("/films/{id}/like/{userId}", filmId, userId);

        ResponseEntity<Long[]> likesResponse = restTemplate.getForEntity("/films/{id}/likes", Long[].class, filmId);
        assertThat(likesResponse.getStatusCodeValue()).isEqualTo(200);
        Long[] likes = likesResponse.getBody();
        assertThat(likes).isNotNull().doesNotContain(userId);
    }

    @DisplayName("11. Проверка, что выводится список фильмов по количеству лайков.")
    @Test
    void shouldReturnFilmsSortedByLikes() {
        UserRegisterDto user1 = UserRegisterDto.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        UserRegisterDto user2 = UserRegisterDto.builder()
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();

        Long userId1 = restTemplate.postForEntity("/users", user1, User.class).getBody().getId();
        Long userId2 = restTemplate.postForEntity("/users", user2, User.class).getBody().getId();

        FilmRegisterDto film1 = FilmRegisterDto.builder()
                .name("Фильм 1")
                .description("Описание 1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        FilmRegisterDto film2 = FilmRegisterDto.builder()
                .name("Фильм 2")
                .description("Описание 2")
                .releaseDate(LocalDate.of(2020, 2, 1))
                .duration(110)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        Long filmId1 = restTemplate.postForEntity("/films", film1, Film.class).getBody().getId();
        Long filmId2 = restTemplate.postForEntity("/films", film2, Film.class).getBody().getId();

        restTemplate.put("/films/{id}/like/{userId}", null, filmId1, userId1);
        restTemplate.put("/films/{id}/like/{userId}", null, filmId1, userId2);
        restTemplate.put("/films/{id}/like/{userId}", null, filmId2, userId1);

        ResponseEntity<FilmDto[]> response = restTemplate.getForEntity("/films/popular?count=2", FilmDto[].class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        FilmDto[] popularFilms = response.getBody();
        assertThat(popularFilms).isNotNull();
        assertThat(popularFilms.length).isEqualTo(2);

        assertThat(popularFilms[0].getId()).isEqualTo(filmId1);
        assertThat(popularFilms[1].getId()).isEqualTo(filmId2);
    }

}
