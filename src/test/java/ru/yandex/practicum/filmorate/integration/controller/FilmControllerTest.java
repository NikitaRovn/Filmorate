package ru.yandex.practicum.filmorate.integration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmRegisterDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;
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
// 12. Проверка, что возвращается фильм по ID.
// 13. Проверка, что фильм обновляется.
// 14. Проверка, что фильм удаляется.
// 15. Проверка, что не обновляет несуществующий фильм.
// 16. Проверка, что не удаляет несуществующий фильм.
// 17. Проверка, что не обновляет фильм с пустым названием.
// 18. Проверка, что не обновляет фильм с длинным (200+) названием.
// 19. Проверка, что не обновляет фильм с отрицательной длительностью.
// 20. Проверка, что не лайкает несуществующий фильм.
// 21. Проверка, что не лайкает фильм несуществующим пользователем.
// 22. Проверка, что если лайков нет - возвращается пустой список популярных фильмов.

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

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", dto, Film.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

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

        ResponseEntity<String> response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        String body = response.getBody();
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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        String body = response.getBody();
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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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

        ResponseEntity<String> response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

        ResponseEntity<String> response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

        ResponseEntity<String> response = restTemplate.postForEntity("/films", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

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
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        assertThat(filmResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Film savedFilm = filmResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        restTemplate.put("/films/{id}/like/{userId}", null, filmId, userId);

        ResponseEntity<Long[]> likesResponse = restTemplate.getForEntity("/films/{id}/likes", Long[].class, filmId);
        assertThat(likesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        assertThat(filmResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Film savedFilm = filmResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        restTemplate.put("/films/{id}/like/{userId}", null, filmId, userId);

        restTemplate.delete("/films/{id}/like/{userId}", filmId, userId);

        ResponseEntity<Long[]> likesResponse = restTemplate.getForEntity("/films/{id}/likes", Long[].class, filmId);
        assertThat(likesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FilmDto[] popularFilms = response.getBody();
        assertThat(popularFilms).isNotNull();
        assertThat(popularFilms.length).isEqualTo(2);

        assertThat(popularFilms[0].getId()).isEqualTo(filmId1);
        assertThat(popularFilms[1].getId()).isEqualTo(filmId2);
    }

    @DisplayName("12. Проверка, что возвращается фильм по ID.")
    @Test
    void shouldReturnFilmById() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм для проверки GET по ID")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(90)
                .mpaRatingId(2L)
                .genresIds(List.of(1L, 3L))
                .build();

        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", dto, Film.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Film savedFilm = postResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        ResponseEntity<Film> getResponse = restTemplate.getForEntity("/films/{id}", Film.class, filmId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Film returnedFilm = getResponse.getBody();
        assertThat(returnedFilm).isNotNull();
        assertThat(returnedFilm.getId()).isEqualTo(filmId);
        assertThat(returnedFilm.getName()).isEqualTo(dto.getName());
        assertThat(returnedFilm.getDescription()).isEqualTo(dto.getDescription());
        assertThat(returnedFilm.getDuration()).isEqualTo(dto.getDuration());

        assertThat(returnedFilm.getGenres())
                .isNotNull()
                .extracting(Genre::getId)
                .containsAll(dto.getGenresIds());

        assertThat(returnedFilm.getMpaRating()).isNotNull();
        assertThat(returnedFilm.getMpaRating().getId()).isEqualTo(dto.getMpaRatingId());
    }

    @DisplayName("13. Проверка, что фильм обновляется.")
    @Test
    void shouldUpdateFilm() {
        FilmRegisterDto originalDto = FilmRegisterDto.builder()
                .name("Оригинальное название")
                .description("Оригинальное описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L, 2L))
                .build();

        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", originalDto, Film.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Film savedFilm = postResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        FilmRegisterDto updateDto = FilmRegisterDto.builder()
                .name("Обновлённое название")
                .description("Обновлённое описание")
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(120)
                .mpaRatingId(2L)
                .genresIds(List.of(2L, 3L))
                .build();

        restTemplate.put("/films/{id}", updateDto, filmId);

        ResponseEntity<Film> getResponse = restTemplate.getForEntity("/films/{id}", Film.class, filmId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Film updatedFilm = getResponse.getBody();
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getId()).isEqualTo(filmId);
        assertThat(updatedFilm.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedFilm.getDescription()).isEqualTo(updateDto.getDescription());
        assertThat(updatedFilm.getDuration()).isEqualTo(updateDto.getDuration());

        assertThat(updatedFilm.getGenres())
                .isNotNull()
                .extracting(Genre::getId)
                .containsAll(updateDto.getGenresIds());

        assertThat(updatedFilm.getMpaRating()).isNotNull();
        assertThat(updatedFilm.getMpaRating().getId()).isEqualTo(updateDto.getMpaRatingId());
    }

    @DisplayName("14. Проверка, что фильм удаляется.")
    @Test
    void shouldDeleteFilm() {
        FilmRegisterDto dto = FilmRegisterDto.builder()
                .name("Фильм для удаления")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L, 2L))
                .build();

        ResponseEntity<Film> postResponse = restTemplate.postForEntity("/films", dto, Film.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Film savedFilm = postResponse.getBody();
        assertThat(savedFilm).isNotNull();
        Long filmId = savedFilm.getId();

        restTemplate.delete("/films/{id}", filmId);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/films/{id}", String.class, filmId);
        assertThat(getResponse.getStatusCodeValue()).isEqualTo(404);
        assertThat(getResponse.getBody()).contains("Фильм не найден.");
    }

    @DisplayName("15. Проверка, что не обновляет несуществующий фильм.")
    @Test
    void shouldReturn404WhenUpdatingNonExistingFilm() {
        FilmUpdateDto updateDto = FilmUpdateDto.builder()
                .name("Обновлённое название")
                .description("Обновлённое описание")
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(120)
                .mpaRatingId(2L)
                .genresIds(List.of(2L, 3L))
                .build();

        Long nonExistingFilmId = 999L;

        ResponseEntity<String> response = restTemplate
                .exchange("/films/{id}",
                        org.springframework.http.HttpMethod.PUT,
                        new org.springframework.http.HttpEntity<>(updateDto),
                        String.class,
                        nonExistingFilmId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).contains("Фильм не найден.");
    }

    @DisplayName("16. Проверка, что не удаляет несуществующий фильм.")
    @Test
    void shouldReturn404WhenDeletingNonExistingFilm() {
        Long nonExistingFilmId = 999L;

        ResponseEntity<String> response = restTemplate
                .exchange("/films/{id}",
                        org.springframework.http.HttpMethod.DELETE,
                        null,
                        String.class,
                        nonExistingFilmId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).contains("Фильм не найден.");
    }

    @DisplayName("17. Проверка, что не обновляет фильм с пустым названием.")
    @Test
    void shouldReturn400WhenUpdatingFilmWithEmptyName() {
        FilmRegisterDto originalDto = FilmRegisterDto.builder()
                .name("Оригинальное название")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        Film savedFilm = restTemplate.postForEntity("/films", originalDto, Film.class).getBody();
        assert savedFilm != null;
        Long filmId = savedFilm.getId();

        FilmUpdateDto updateDto = FilmUpdateDto.builder()
                .name("   ")
                .description("Обновлённое описание")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<String> response = restTemplate
                .exchange("/films/{id}",
                        org.springframework.http.HttpMethod.PUT,
                        new org.springframework.http.HttpEntity<>(updateDto),
                        String.class,
                        filmId);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("name");
    }

    @DisplayName("18. Проверка, что не обновляет фильм с длинным (200+) названием.")
    @Test
    void shouldReturn400WhenUpdatingFilmWithTooLongDescription() {
        FilmRegisterDto originalDto = FilmRegisterDto.builder()
                .name("Оригинальный фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        Film savedFilm = restTemplate.postForEntity("/films", originalDto, Film.class).getBody();
        assert savedFilm != null;
        Long filmId = savedFilm.getId();

        String longDescription = "A".repeat(201);
        FilmUpdateDto updateDto = FilmUpdateDto.builder()
                .name("Фильм")
                .description(longDescription)
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<String> response = restTemplate
                .exchange("/films/{id}",
                        org.springframework.http.HttpMethod.PUT,
                        new org.springframework.http.HttpEntity<>(updateDto),
                        String.class,
                        filmId);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("description");
    }

    @DisplayName("19. Проверка, что не обновляет фильм с отрицательной длительностью.")
    @Test
    void shouldReturn400WhenUpdatingFilmWithNegativeDuration() {
        FilmRegisterDto originalDto = FilmRegisterDto.builder()
                .name("Оригинальный фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        Film savedFilm = restTemplate.postForEntity("/films", originalDto, Film.class).getBody();
        assert savedFilm != null;
        Long filmId = savedFilm.getId();

        FilmUpdateDto updateDto = FilmUpdateDto.builder()
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(-50)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        ResponseEntity<String> response = restTemplate
                .exchange("/films/{id}",
                        org.springframework.http.HttpMethod.PUT,
                        new org.springframework.http.HttpEntity<>(updateDto),
                        String.class,
                        filmId);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("duration");
    }

    @DisplayName("20. Проверка, что не лайкает несуществующий фильм.")
    @Test
    void shouldReturn404WhenLikingNonExistingFilm() {
        UserRegisterDto userDto = UserRegisterDto.builder()
                .email("user@example.com")
                .login("userlogin")
                .name("User Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Long userId = restTemplate.postForEntity("/users", userDto, User.class).getBody().getId();

        Long nonExistingFilmId = 999L;

        ResponseEntity<String> response = restTemplate.exchange(
                "/films/{id}/like/{userId}",
                org.springframework.http.HttpMethod.PUT,
                null,
                String.class,
                nonExistingFilmId,
                userId
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).contains("Фильм не найден.");
    }

    @DisplayName("21. Проверка, что не лайкает фильм несуществующим пользователем.")
    @Test
    void shouldReturn404WhenLikingByNonExistingUser() {
        FilmRegisterDto filmDto = FilmRegisterDto.builder()
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpaRatingId(1L)
                .genresIds(List.of(1L))
                .build();

        Long filmId = restTemplate.postForEntity("/films", filmDto, Film.class).getBody().getId();

        Long nonExistingUserId = 999L;

        ResponseEntity<String> response = restTemplate.exchange(
                "/films/{id}/like/{userId}",
                org.springframework.http.HttpMethod.PUT,
                null,
                String.class,
                filmId,
                nonExistingUserId
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).contains("Пользователь не найден.");
    }

    @DisplayName("22. Проверка, что если лайков нет - возвращается пустой список популярных фильмов.")
    @Test
    void shouldReturnEmptyListWhenNoLikesExist() {
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
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(110)
                .mpaRatingId(1L)
                .genresIds(List.of(2L))
                .build();

        FilmRegisterDto film3 = FilmRegisterDto.builder()
                .name("Фильм 3")
                .description("Описание 3")
                .releaseDate(LocalDate.of(2022, 3, 3))
                .duration(120)
                .mpaRatingId(1L)
                .genresIds(List.of(3L))
                .build();

        restTemplate.postForEntity("/films", film1, Film.class);
        restTemplate.postForEntity("/films", film2, Film.class);
        restTemplate.postForEntity("/films", film3, Film.class);

        ResponseEntity<FilmDto[]> response = restTemplate.getForEntity("/films/popular?count=3", FilmDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FilmDto[] popularFilms = response.getBody();
        assertThat(popularFilms).isNotNull();
        assertThat(popularFilms.length).isEqualTo(0);
    }

}
