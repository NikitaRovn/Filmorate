package ru.yandex.practicum.filmorate.integration.controller;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserRegisterDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

// Список тестов:
// 1. Проверка, что регистрирует пользователя.
// 2. Проверка, что регистрирует пользователя с пустым именем.
// 3. Проверка, что не регистрирует пользователя с пробелом в логине.
// 4. Проверка, что не регистрирует пользователя, если почта не содержит @.
// 5. Проверка, что не регистрирует пользователя, если логин пустой.
// 6. Проверка, что не регистрирует пользователя, если логин не передан.
// 7. Проверка, что пользователя не регистрирует, если дата рождения позже текущей.
// 8. Проверка, что сервер отдает список пользователей корректно.
// 9. Проверка, что сервер обновляет пользователя корректно.
// 10. Проверка, что пользователи могут стать друзьями.
// 11. Проверка, что пользователь может удалить друга.
// 12. Проверка, что возвращается список друзей.
// 13. Проверка, что возвращается список общих друзей.
// 14. Проверка, что возвращается пользователь по ID.
// 15. Проверка, что возвращается ошибка 404 при запросе несуществующего пользователя.
// 16. Проверка, что возвращается 404 при добавлении в друзья с несуществующим пользователем.
// 17. Проверка, что нельзя удалить несуществующего пользователя.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @DisplayName("1. Проверка, что регистрирует пользователя")
    @Test
    void shouldRegisterUser() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login("testuser1")
                .email("testuser1@example.com")
                .name("Тестовый пользователь 1")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", dto, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto savedUser = response.getBody();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo(dto.getLogin());
        assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(savedUser.getName()).isEqualTo(dto.getName());
        assertThat(savedUser.getBirthday()).isEqualTo(dto.getBirthday());
    }

    @DisplayName("2. Проверка, что регистрирует пользователя с пустым именем и подставляет логин")
    @Test
    void shouldRegisterUserWithEmptyNameAndSetLoginAsName() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login("testuser2")
                .email("testuser2@example.com")
                .name("")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", dto, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto savedUser = response.getBody();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo(dto.getLogin());

        assertThat(savedUser.getName()).isEqualTo(dto.getLogin());

        assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(savedUser.getBirthday()).isEqualTo(dto.getBirthday());
    }

    @DisplayName("3. Проверка, что не регистрирует пользователя с пробелом в логине")
    @Test
    void shouldNotRegisterUserWithSpaceInLogin() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login("invalid login")
                .email("user3@example.com")
                .name("User Three")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/users", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        assertThat(response.getBody()).contains("login");
    }

    @DisplayName("4. Проверка, что не регистрирует пользователя, если почта не содержит @")
    @Test
    void shouldNotRegisterUserWithInvalidEmail() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login("user4")
                .email("user4example.com")
                .name("User Four")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/users", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        assertThat(response.getBody()).contains("email");
    }

    @DisplayName("5. Проверка, что не регистрирует пользователя, если логин пустой")
    @Test
    void shouldNotRegisterUserWithEmptyLogin() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login("   ")
                .email("user5@example.com")
                .name("User Five")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/users", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        assertThat(response.getBody()).contains("login");
    }

    @DisplayName("6. Проверка, что не регистрирует пользователя, если логин не передан")
    @Test
    void shouldNotRegisterUserWithoutLogin() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login(null)
                .email("user6@example.com")
                .name("User Six")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/users", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        assertThat(response.getBody()).contains("login");
    }

    @DisplayName("7. Проверка, что не регистрирует пользователя с будущей датой рождения")
    @Test
    void shouldNotRegisterUserWithFutureBirthday() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .login("futureUser")
                .email("future@example.com")
                .name("Future User")
                .birthday(LocalDate.parse(LocalDate.now().plusDays(1).toString()))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/users", dto, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        assertThat(response.getBody()).contains("birthday");
    }

    @DisplayName("8. Проверка, что сервер отдает список пользователей корректно")
    @Test
    void shouldReturnListOfUsers() {
        UserRegisterDto dto1 = UserRegisterDto.builder()
                .login("user1")
                .email("user1@example.com")
                .name("User Name 1")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        UserRegisterDto dto2 = UserRegisterDto.builder()
                .login("user2")
                .email("user2@example.com")
                .name("User Name 2")
                .birthday(LocalDate.parse("1991-02-02"))
                .build();

        UserRegisterDto dto3 = UserRegisterDto.builder()
                .login("user3")
                .email("user3@example.com")
                .name("User Name 3")
                .birthday(LocalDate.parse("1992-03-03"))
                .build();

        restTemplate.postForEntity("/users", dto1, String.class);
        restTemplate.postForEntity("/users", dto2, String.class);
        restTemplate.postForEntity("/users", dto3, String.class);

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity("/users", UserDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto[] users = response.getBody();
        assertThat(users).isNotNull()
                .hasSize(3);

        List<String> logins = Arrays.stream(users)
                .map(UserDto::getLogin)
                .toList();

        assertThat(logins).containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @DisplayName("9. Проверка, что сервер обновляет пользователя корректно")
    @Test
    void shouldUpdateUser() {
        UserRegisterDto registerDto = UserRegisterDto.builder()
                .login("userUpdate")
                .email("update@example.com")
                .name("Original Name")
                .birthday(LocalDate.parse("1990-01-01"))
                .build();

        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/users", registerDto, UserDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        Long userId = createdUser.getId();

        UserUpdateDto updateDto = UserUpdateDto.builder()
                .login("userUpdated")
                .email("updated@example.com")
                .name("Updated Name")
                .birthday(LocalDate.parse("1991-02-02"))
                .build();

        restTemplate.put("/users/{id}", updateDto, userId);

        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity("/users/{id}", UserDto.class, userId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto updatedUser = getResponse.getBody();
        assertThat(updatedUser).isNotNull()
                .extracting(UserDto::getLogin, UserDto::getEmail, UserDto::getName, UserDto::getBirthday)
                .containsExactly("userUpdated", "updated@example.com", "Updated Name", LocalDate.of(1991, 2, 2));
    }

    @LocalServerPort
    private int port;

    @DisplayName("10. Проверка, что пользователи могут стать друзьями.")
    @Test
    void shouldAddFriends() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        String baseUrl = "http://localhost:" + port;

        UserRegisterDto user1Dto = UserRegisterDto.builder()
                .login("Тестюзер")
                .email("Тестюзер@example.com")
                .name("Тестюзер")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserRegisterDto user2Dto = UserRegisterDto.builder()
                .login("Тестюзер2")
                .email("Тестюзер2@example.com")
                .name("Тестюзер2")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();

        UserDto user1 = restTemplate.postForEntity(baseUrl + "/users", user1Dto, UserDto.class).getBody();
        UserDto user2 = restTemplate.postForEntity(baseUrl + "/users", user2Dto, UserDto.class).getBody();

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user1.getId(), user2.getId());

        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}",
                HttpMethod.PATCH, null, Void.class, user2.getId(), user1.getId());

        List<UserDto> user1Friends = List.of(restTemplate.getForObject(baseUrl + "/users/{id}/friends", UserDto[].class, user1.getId()));
        List<UserDto> user2Friends = List.of(restTemplate.getForObject(baseUrl + "/users/{id}/friends", UserDto[].class, user2.getId()));

        assertThat(user1Friends)
                .extracting(UserDto::getId, UserDto::getLogin)
                .containsExactly(tuple(user2.getId(), "Тестюзер2"));

        assertThat(user2Friends)
                .extracting(UserDto::getId, UserDto::getLogin)
                .containsExactly(tuple(user1.getId(), "Тестюзер"));
    }

    @DisplayName("11. Проверка, что пользователь может удалить друга.")
    @Test
    void shouldRemoveFriend() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        String baseUrl = "http://localhost:" + port;

        UserRegisterDto user1Dto = UserRegisterDto.builder()
                .login("Тестюзер")
                .email("Тестюзер@example.com")
                .name("Тестюзер")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserRegisterDto user2Dto = UserRegisterDto.builder()
                .login("Тестюзер2")
                .email("Тестюзер2@example.com")
                .name("Тестюзер2")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();

        UserDto user1 = restTemplate.postForEntity(baseUrl + "/users", user1Dto, UserDto.class).getBody();
        UserDto user2 = restTemplate.postForEntity(baseUrl + "/users", user2Dto, UserDto.class).getBody();

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user1.getId(), user2.getId());

        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}",
                HttpMethod.PATCH, null, Void.class, user2.getId(), user1.getId());

        List<UserDto> user1FriendsBefore = List.of(restTemplate.getForObject(baseUrl + "/users/{id}/friends", UserDto[].class, user1.getId()));
        assertThat(user1FriendsBefore)
                .extracting(UserDto::getId)
                .contains(user2.getId());

        restTemplate.delete(baseUrl + "/users/{id}/friends/{friendId}", user1.getId(), user2.getId());

        List<UserDto> user1FriendsAfter = List.of(restTemplate.getForObject(baseUrl + "/users/{id}/friends", UserDto[].class, user1.getId()));
        List<UserDto> user2FriendsAfter = List.of(restTemplate.getForObject(baseUrl + "/users/{id}/friends", UserDto[].class, user2.getId()));

        assertThat(user1FriendsAfter).extracting(UserDto::getId).doesNotContain(user2.getId());
        assertThat(user2FriendsAfter).extracting(UserDto::getId).doesNotContain(user1.getId());
    }

    @DisplayName("12. Проверка, что возвращается список друзей.")
    @Test
    void shouldReturnFriendsList() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        String baseUrl = "http://localhost:" + port;

        UserRegisterDto user1Dto = UserRegisterDto.builder()
                .login("Тестюзер")
                .email("Тестюзер@example.com")
                .name("Тестюзер")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserRegisterDto user2Dto = UserRegisterDto.builder()
                .login("Тестюзер2")
                .email("Тестюзер2@example.com")
                .name("Тестюзер2")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        UserRegisterDto user3Dto = UserRegisterDto.builder()
                .login("Тестюзер3")
                .email("Тестюзер3@example.com")
                .name("Тестюзер3")
                .birthday(LocalDate.of(1992, 3, 3))
                .build();

        UserDto user1 = restTemplate.postForEntity(baseUrl + "/users", user1Dto, UserDto.class).getBody();
        UserDto user2 = restTemplate.postForEntity(baseUrl + "/users", user2Dto, UserDto.class).getBody();
        UserDto user3 = restTemplate.postForEntity(baseUrl + "/users", user3Dto, UserDto.class).getBody();

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user1.getId(), user2.getId());
        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}",
                HttpMethod.PATCH, null, Void.class, user2.getId(), user1.getId());

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user1.getId(), user3.getId());
        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}",
                HttpMethod.PATCH, null, Void.class, user3.getId(), user1.getId());

        List<UserDto> friendsOfUser1 = List.of(restTemplate.getForObject(baseUrl + "/users/{id}/friends", UserDto[].class, user1.getId()));

        assertThat(friendsOfUser1)
                .extracting(UserDto::getId, UserDto::getLogin)
                .containsExactlyInAnyOrder(
                        tuple(user2.getId(), "Тестюзер2"),
                        tuple(user3.getId(), "Тестюзер3")
                );
    }

    @DisplayName("13. Проверка, что возвращается список общих друзей.")
    @Test
    void shouldReturnMutualFriends() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        String baseUrl = "http://localhost:" + port;

        UserRegisterDto user1Dto = UserRegisterDto.builder()
                .login("Тестюзер")
                .email("Тестюзер@example.com")
                .name("Тестюзер")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserRegisterDto user2Dto = UserRegisterDto.builder()
                .login("Тестюзер2")
                .email("Тестюзер2@example.com")
                .name("Тестюзер2")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        UserRegisterDto user3Dto = UserRegisterDto.builder()
                .login("Тестюзер3")
                .email("Тестюзер3@example.com")
                .name("Тестюзер3")
                .birthday(LocalDate.of(1992, 3, 3))
                .build();
        UserRegisterDto user4Dto = UserRegisterDto.builder()
                .login("Тестюзер4")
                .email("Тестюзер4@example.com")
                .name("Тестюзер4")
                .birthday(LocalDate.of(1993, 4, 4))
                .build();

        UserDto user1 = restTemplate.postForEntity(baseUrl + "/users", user1Dto, UserDto.class).getBody();
        UserDto user2 = restTemplate.postForEntity(baseUrl + "/users", user2Dto, UserDto.class).getBody();
        UserDto user3 = restTemplate.postForEntity(baseUrl + "/users", user3Dto, UserDto.class).getBody();
        UserDto user4 = restTemplate.postForEntity(baseUrl + "/users", user4Dto, UserDto.class).getBody();

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user1.getId(), user3.getId());
        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}", HttpMethod.PATCH, null, Void.class, user3.getId(), user1.getId());

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user2.getId(), user3.getId());
        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}", HttpMethod.PATCH, null, Void.class, user3.getId(), user2.getId());

        restTemplate.put(baseUrl + "/users/{id}/friends/{friendId}", null, user1.getId(), user4.getId());
        restTemplate.exchange(baseUrl + "/users/{id}/friends/{friendId}", HttpMethod.PATCH, null, Void.class, user4.getId(), user1.getId());

        List<UserDto> mutualFriends = List.of(
                restTemplate.getForObject(baseUrl + "/users/{id}/friends/common/{otherId}", UserDto[].class, user1.getId(), user2.getId())
        );

        assertThat(mutualFriends)
                .extracting(UserDto::getId, UserDto::getLogin)
                .containsExactly(
                        tuple(user3.getId(), "Тестюзер3")
                );
    }

    @DisplayName("14. Проверка, что возвращается пользователь по ID.")
    @Test
    void shouldReturnUserById() {
        UserRegisterDto registerDto = UserRegisterDto.builder()
                .email("Тестюзер@example.com")
                .login("Тестюзер")
                .name("Тест юзер")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", registerDto, User.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        User createdUser = createResponse.getBody();
        AssertionsForClassTypes.assertThat(createdUser).isNotNull();
        AssertionsForClassTypes.assertThat(createdUser.getId()).isNotNull();

        Long userId = createdUser.getId();

        ResponseEntity<User> getResponse = restTemplate.getForEntity("/users/{id}", User.class, userId);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        User returnedUser = getResponse.getBody();
        AssertionsForClassTypes.assertThat(returnedUser).isNotNull();
        AssertionsForClassTypes.assertThat(returnedUser.getId()).isEqualTo(userId);
        AssertionsForClassTypes.assertThat(returnedUser.getEmail()).isEqualTo(registerDto.getEmail());
        AssertionsForClassTypes.assertThat(returnedUser.getLogin()).isEqualTo(registerDto.getLogin());
        AssertionsForClassTypes.assertThat(returnedUser.getName()).isEqualTo(registerDto.getName());
        AssertionsForClassTypes.assertThat(returnedUser.getBirthday()).isEqualTo(registerDto.getBirthday());
    }

    @DisplayName("15. Проверка, что возвращается ошибка 404 при запросе несуществующего пользователя.")
    @Test
    void shouldReturnNotFoundForInvalidUserId() {
        Long invalidId = 999L;

        ResponseEntity<String> response = restTemplate.getForEntity("/users/{id}", String.class, invalidId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        String body = response.getBody();

        assertThat(body)
                .isNotNull()
                .contains("user")
                .contains("Пользователь не найден.");
    }

    @DisplayName("16. Проверка, что возвращается 404 при добавлении в друзья с несуществующим пользователем.")
    @Test
    void shouldReturnNotFoundWhenAddingFriendWithInvalidIds() {
        UserRegisterDto userDto = UserRegisterDto.builder()
                .email("friendcheck@example.com")
                .login("friendcheck")
                .name("Friend Checker")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/users", userDto, UserDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long existingUserId = createResponse.getBody().getId();

        Long invalidUserId = 999L;

        ResponseEntity<String> response = restTemplate.exchange(
                "/users/{id}/friends/{friendId}",
                HttpMethod.PUT,
                null,
                String.class,
                existingUserId,
                invalidUserId
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @DisplayName("17. Проверка, что нельзя удалить несуществующего пользователя.")
    @Test
    void shouldReturn404WhenDeletingNonExistingUser() {
        Long nonExistingId = 999L;

        ResponseEntity<String> response = restTemplate.exchange(
                "/users/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                nonExistingId
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull().contains("Not Found");
        assertThat(response.getBody()).isNotNull().contains("Пользователь не найден.");
    }
}
