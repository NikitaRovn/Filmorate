package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        log.trace("Начало метода registerUser, на входе пользователь: {}", user);

        // email validation
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Провал валидации поля email: пустая.");
            throw new UserValidationException("Электронная почта не может быть пустой.");
        } else if (!user.getEmail().contains("@")) {
            log.warn("Провал валидации поля email: нет символа @.");
            throw new UserValidationException("Электронная почта должна содержать символ @.");
        }

        // login validation
        String login = user.getLogin();
        if (login == null || login.isBlank()) {
            log.warn("Провал валидации поля login: пустой.");
            throw new UserValidationException("Логин не может быть пустой.");
        } else if (user.getLogin().contains(" ")) {
            log.warn("Провал валидации поля login: содержит пробел.");
            throw new UserValidationException("Логин не может содержать пробелы.");
        }

        // name validation
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Поле name пустое или null, подставлено поле login.");
            user.setName(user.getLogin());
        }

        // birthday validation
        LocalDate birthday = user.getBirthday();
        if (birthday == null) {
            log.warn("Провал валидации поля birthday: пустое.");
            throw new UserValidationException("Дата рождения должна быть.");
        }
        LocalDate now = LocalDate.now();
        if (now.isBefore(birthday)) {
            log.warn("Провал валидации поля birthday: после текущей даты, дата: {}", birthday);
            throw new UserValidationException("Дата рождения не может быть в будущем.");
        }

        log.debug("Начинается сохранение пользователя: {}", user);
        User registeredUser = userRepository.save(user);
        log.info("Пользователь успешно сохранен и возвращен: {}", registeredUser);
        return registeredUser;
    }

    public User getUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не существует.");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User userToUpdate) {
        log.trace("Начало метода updateUser, на входе пользователь: {}", userToUpdate);
        Long id = userToUpdate.getId();
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("Попытка обновить данные пользователя, пользователь не найден, id: {}", id);
            throw new UserNotFoundException("Пользователя с id: " + id + " не существует.");
        }
        log.debug("Начинается обновление пользователя: {}", userToUpdate);
        User updatedUser = userRepository.update(userToUpdate);
        log.info("Пользователь успешно обновлен и возвращен: {}", updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        log.trace("Начало метода deleteUser, на входе пользователь с id: {}", id);
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("Попытка удалить пользователя, пользователь не найден, id: {}", id);
            throw new UserNotFoundException("Пользователя с id: " + id + " не существует.");
        }
        log.debug("Начинается удаление пользователя с id: {}", id);
        User deletedUser = userRepository.deleteById(id);
        log.info("Пользователь успешно удален и возвращен: {}", deletedUser);
    }
}
