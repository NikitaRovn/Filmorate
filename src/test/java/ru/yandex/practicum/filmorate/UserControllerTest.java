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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Список тестов:
// 1. Проверка, что регистрирует пользователя.
// 2. Проверка, что регистрирует пользователя с пустым именем.
// 3. Проверка, что не регистрирует пользователя с пробелом в логине.
// 4. Проверка, что не регистрирует пользователя, если почта не содержит @.
// 5. Проверка, что не регистрирует пользователя, если логин пустой.
// 6. Проверка, что пользователя не регистрирует, если дата рождения позже текущей.
// 7. Проверка, что сервер отдает список пользователей корректно.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

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

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                userBadLogin,
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                userBadEmail,
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                userNoLogin,
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/users",
                userBadBirthday,
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
}
