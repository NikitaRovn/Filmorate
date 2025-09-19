package ru.yandex.practicum.filmorate.integration.controller;

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
import ru.yandex.practicum.filmorate.repository.friends.InMemoryFriendsRepository;
import ru.yandex.practicum.filmorate.repository.user.InMemoryUserRepository;

import java.time.LocalDate;
import java.util.List;

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
// 9. Проверка, что пользователи могут стать друзьями.
// 10. Проверка, что пользователь может удалить друга.
// 11. Проверка, что возвращается список друзей.
// 12. Проверка, что возвращается список общих друзей.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private InMemoryUserRepository userRepository;

    @Autowired
    private InMemoryFriendsRepository friendsRepository;

    @AfterEach
    void tearDown() {
        userRepository.clear();
        friendsRepository.clear();
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

    @DisplayName("9. Проверка, что пользователи могут стать друзьями.")
    @Test
    void shouldSendFriendRequest() {
        User user1 = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response1 = testRestTemplate.postForEntity(
                "/users",
                user1,
                User.class);

        Long user1Id = response1.getBody().getId();

        User user2 = User.builder()
                .name("Name2")
                .email("Email2@gmail.com")
                .login("Login2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response2 = testRestTemplate.postForEntity(
                "/users",
                user2,
                User.class);

        Long user2Id = response2.getBody().getId();

        testRestTemplate.put("/users/{user1Id}/friends/{user2Id}", null, user1Id, user2Id);

        friendsRepository.acceptFriendship(user2Id, user1Id);

        List<Long> friends = friendsRepository.findFriendsById(user1Id);

        assertThat(friends).hasSize(1);
        assertThat(friends).isEqualTo(List.of(user2Id));
    }


    @DisplayName("10. Проверка, что пользователь может удалить друга.")
    @Test
    void shouldDeleteFriend() {
        User user1 = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response1 = testRestTemplate.postForEntity(
                "/users",
                user1,
                User.class);

        Long user1Id = response1.getBody().getId();

        User user2 = User.builder()
                .name("Name2")
                .email("Email2@gmail.com")
                .login("Login2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response2 = testRestTemplate.postForEntity(
                "/users",
                user2,
                User.class);

        Long user2Id = response2.getBody().getId();

        testRestTemplate.put("/users/{user1Id}/friends/{user2Id}",
                null,
                user1Id,
                user2Id);

        friendsRepository.acceptFriendship(user2Id, user1Id);

        List<Long> friends = friendsRepository.findFriendsById(user1Id);

        assertThat(friends).hasSize(1);
        assertThat(friends).isEqualTo(List.of(user2Id));

        testRestTemplate.delete("/users/{user1Id}/friends/{user2Id}",
                user1Id,
                user2Id);

        friends = friendsRepository.findFriendsById(user1Id);

        assertThat(friends).hasSize(0);
    }

    @DisplayName("11. Проверка, что возвращается список друзей.")
    @Test
    void shouldReturnFriendsList() {
        User user1 = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response1 = testRestTemplate.postForEntity(
                "/users",
                user1,
                User.class);

        Long user1Id = response1.getBody().getId();

        User user2 = User.builder()
                .name("Name2")
                .email("Email2@gmail.com")
                .login("Login2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response2 = testRestTemplate.postForEntity(
                "/users",
                user2,
                User.class);

        Long user2Id = response2.getBody().getId();

        testRestTemplate.put("/users/{user1Id}/friends/{user2Id}",
                null,
                user1Id,
                user2Id);

        friendsRepository.acceptFriendship(user2Id, user1Id);

        ResponseEntity<User[]> friendsResponse = testRestTemplate.getForEntity(
                "/users/{user1Id}/friends",
                User[].class,
                user1Id);

        assertThat(friendsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(friendsResponse.getBody()).isNotNull();
        assertThat(friendsResponse.getBody()).hasSize(1);
        assertThat(friendsResponse.getBody()[0].getId()).isEqualTo(user2Id);
    }

    @DisplayName("12. Проверка, что возвращается список общих друзей.")
    @Test
    void shouldReturnCommonFriends() {
        User user1 = User.builder()
                .name("Name1")
                .email("Email1@gmail.com")
                .login("Login1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response1 = testRestTemplate.postForEntity(
                "/users",
                user1,
                User.class);

        Long user1Id = response1.getBody().getId();

        User user2 = User.builder()
                .name("Name2")
                .email("Email2@gmail.com")
                .login("Login2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response2 = testRestTemplate.postForEntity(
                "/users",
                user2,
                User.class);

        Long user2Id = response2.getBody().getId();

        User user3 = User.builder()
                .name("Name3")
                .email("Email3@gmail.com")
                .login("Login3")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ResponseEntity<User> response3 = testRestTemplate.postForEntity(
                "/users",
                user3,
                User.class);

        Long user3Id = response3.getBody().getId();

        testRestTemplate.put("/users/{user1Id}/friends/{user3Id}",
                null,
                user1Id,
                user3Id);
        friendsRepository.acceptFriendship(user3Id, user1Id);

        testRestTemplate.put("/users/{user2Id}/friends/{user3Id}",
                null,
                user2Id,
                user3Id);
        friendsRepository.acceptFriendship(user3Id, user2Id);

        ResponseEntity<User[]> commonFriendsResponse = testRestTemplate.getForEntity(
                "/users/{user1Id}/friends/common/{user2Id}",
                User[].class,
                user1Id,
                user2Id);

        assertThat(commonFriendsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(commonFriendsResponse.getBody()).isNotNull();
        assertThat(commonFriendsResponse.getBody()).hasSize(1);
        assertThat(commonFriendsResponse.getBody()[0].getId()).isEqualTo(user3Id);
    }
}
