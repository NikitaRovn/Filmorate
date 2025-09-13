package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.InMemoryUserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Список тестов:
// 1. Проверка, что регистрирует пользователя.
// 2. Проверка, что регистрирует пользователя с пустым именем.
// 3. Проверка, что не регистрирует пользователя с пробелом в логине.
// 4. Проверка, что не регистрирует пользователя, если почта не содержит @.
// 5. Проверка, что не регистрирует пользователя, если логин пустой.
// 5.1 Проверка, что не регистрирует пользователя, если логин не передан.
// 6. Проверка, что пользователя не регистрирует, если дата рождения позже текущей.
// 7. Проверка, что сервер отдает список пользователей корректно.
// 8. Проверка, что сервер обновляет пользователя корректно.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private InMemoryUserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.clear();
    }

    @DisplayName("1. Проверка, что регистрирует пользователя.")
    @Test
    void shouldRegisterUser() {
        User user = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                user,
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        User registeredUser = response.getBody();

        assertThat(registeredUser)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @DisplayName("2. Проверка, что регистрирует пользователя с пустым именем.")
    @Test
    void shouldSetNameAsLoginIfNameEmpty() {
        User userNoName = User.builder()
                .name("")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                userNoName,
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Login1");
    }

    @DisplayName("3. Проверка, что не регистрирует пользователя с пробелом в логине.")
    @Test
    void shouldRejectRegisterUserIfLoginWithSpace() {
        User userBadLogin = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login 1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<ValidationErrorResponse> response = testRestTemplate.postForEntity(
                "/users",
                userBadLogin,
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrors()).isNotEmpty();
        assertThat(response.getBody().getErrors())
                .anyMatch(e -> e.getField().equals("login"));
    }

    @DisplayName("4. Проверка, что не регистрирует пользователя, если почта не содержит @.")
    @Test
    void shouldRejectRegisterUserIfEmailInvalid() {
        User userBadEmail = User.builder()
                .name("Name1")
                .email("thisisdog.ru")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<ValidationErrorResponse> response = testRestTemplate.postForEntity(
                "/users",
                userBadEmail,
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrors()).isNotEmpty();
        assertThat(response.getBody().getErrors())
                .anyMatch(e -> e.getField().equals("email"));
    }

    @DisplayName("5. Проверка, что не регистрирует пользователя, если логин пустой.")
    @Test
    void shouldRejectRegisterUserIfLoginIsBlank() {
        User userNoLogin = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<ValidationErrorResponse> response = testRestTemplate.postForEntity(
                "/users",
                userNoLogin,
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrors()).isNotEmpty();
        assertThat(response.getBody().getErrors())
                .anyMatch(e -> e.getField().equals("login"));
    }

    @DisplayName("5.1 Проверка, что не регистрирует пользователя, если логин не передан.")
    @Test
    void shouldRejectRegisterUserIfLoginIsNull() {
        User userNoLogin = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<ValidationErrorResponse> response = testRestTemplate.postForEntity(
                "/users",
                userNoLogin,
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrors()).isNotEmpty();
        assertThat(response.getBody().getErrors()).anyMatch(e -> e.getField().equals("login"));
    }

    @DisplayName("6. Проверка, что пользователя не регистрирует, если дата рождения позже текущей.")
    @Test
    void shouldRejectRegisterUserIfBirthdayInvalid() {
        User userBadBirthday = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2100, 1, 1))
                .build();

        ResponseEntity<ValidationErrorResponse> response = testRestTemplate.postForEntity(
                "/users",
                userBadBirthday,
                ValidationErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrors()).isNotEmpty();
        assertThat(response.getBody().getErrors())
                .anyMatch(e -> e.getField().equals("birthday"));
    }

    @DisplayName("7. Проверка, что сервер отдает список пользователей корректно.")
    @Test
    void shouldGetAllUsers() {
        User user1 = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        testRestTemplate.postForEntity(
                "/users",
                user1,
                User.class);

        User user2 = User.builder()
                .name("Name2")
                .email("Email2@gmail.com")
                .login("Login2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        testRestTemplate.postForEntity(
                "/users",
                user2,
                User.class);

        ResponseEntity<User[]> response = testRestTemplate.getForEntity("/users", User[].class);
        User[] users = response.getBody();

        assertThat(users).hasSize(2);
    }

    @DisplayName("8. Проверка, что сервер обновляет пользователя корректно.")
    @Test
    void shouldUpdateUserData() {
        User userOld = User.builder()
                .name("NameOld")
                .email("EmailOld@gmail.com")
                .login("LoginOld")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                userOld,
                User.class);

        Long id = response.getBody().getId();

        User userNew = User.builder()
                .name("NameNew")
                .email("EmailNew@gmail.com")
                .login("LoginNew")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();

        testRestTemplate.put("/users/{id}", userNew, id);

        response = testRestTemplate.getForEntity("/users/{id}", User.class, id);

        userNew.setId(id);

        assertThat(userNew).usingRecursiveComparison().isEqualTo(response.getBody());
    }
}
