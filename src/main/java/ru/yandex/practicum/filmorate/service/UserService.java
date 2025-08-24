package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserRegisterDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.logging.LogMessages;
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
        log.trace(LogMessages.USER_ADD, userRegisterDto);

        User user = User.builder()
                .login(userRegisterDto.getLogin())
                .email(userRegisterDto.getEmail())
                .birthday(userRegisterDto.getBirthday())
                .name(userRegisterDto.getName() == null || userRegisterDto.getName().isBlank() ? userRegisterDto.getLogin() : userRegisterDto.getName())
                .build();

        log.debug(LogMessages.USER_SAVE_STARTED, user);
        User registeredUser = userRepository.save(user);
        log.info(LogMessages.USER_SAVE_SUCCESS, registeredUser);
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
        log.trace(LogMessages.USER_UPDATE, userUpdateDto);
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn(LogMessages.USER_UPDATE_NOT_FOUND, id);
            throw new UserNotFoundException(id);
        }
        log.debug(LogMessages.USER_UPDATE_STARTED, id);
        user.setName(userUpdateDto.getName());
        user.setLogin(userUpdateDto.getLogin());
        user.setBirthday(userUpdateDto.getBirthday());
        user.setEmail(userUpdateDto.getEmail());

        User updatedUser = userRepository.update(user);
        log.info(LogMessages.USER_UPDATE_SUCCESS, updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        log.trace(LogMessages.USER_DELETE, id);
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn(LogMessages.USER_DELETE_NOT_FOUND, id);
            throw new UserNotFoundException(id);
        }
        log.debug(LogMessages.USER_DELETE_STARTED, id);
        User deletedUser = userRepository.deleteById(id);
        log.info(LogMessages.USER_DELETE_SUCCESS, deletedUser);
    }
}
