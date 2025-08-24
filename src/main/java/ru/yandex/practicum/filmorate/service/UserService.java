package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserRegisterDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        log.trace("Начало метода registerUser, на входе пользователь: {}", userRegisterDto);

        User user = User.builder()
                .login(userRegisterDto.getLogin())
                .email(userRegisterDto.getEmail())
                .birthday(userRegisterDto.getBirthday())
                .name(userRegisterDto.getName() == null || userRegisterDto.getName().isBlank() ? userRegisterDto.getLogin() : userRegisterDto.getName())
                .build();

        log.debug("Начинается сохранение пользователя: {}", user);
        User registeredUser = userRepository.save(user);
        log.info("Пользователь успешно сохранен и возвращен: {}", registeredUser);
        return registeredUser;
    }

    public User getUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(UserUpdateDto userUpdateDto, Long id) {
        log.trace("Начало метода updateUser, на входе пользователь: {}, id: {}", userUpdateDto, id);
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("Попытка обновить данные пользователя, пользователь не найден, id: {}", id);
            throw new UserNotFoundException(id);
        }
        log.debug("Начинается обновление пользователя: {}", user);
        user.setName(userUpdateDto.getName());
        user.setLogin(userUpdateDto.getLogin());
        user.setBirthday(userUpdateDto.getBirthday());
        user.setEmail(userUpdateDto.getEmail());

        User updatedUser = userRepository.update(user);
        log.info("Пользователь успешно обновлен и возвращен: {}", updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        log.trace("Начало метода deleteUser, на входе пользователь с id: {}", id);
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("Попытка удалить пользователя, пользователь не найден, id: {}", id);
            throw new UserNotFoundException(id);
        }
        log.debug("Начинается удаление пользователя с id: {}", id);
        User deletedUser = userRepository.deleteById(id);
        log.info("Пользователь успешно удален и возвращен: {}", deletedUser);
    }
}
