package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserRegisterDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;
import ru.yandex.practicum.filmorate.logging.LogMessages;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friends.FriendsRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;
    private final EntityValidator entityValidator;

    public UserService(@Qualifier("jdbcUserRepository") UserRepository userRepository,
                       @Qualifier("jdbcFriendsRepository") FriendsRepository friendsRepository,
                       EntityValidator entityValidator) {
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
        this.entityValidator = entityValidator;
    }

    public UserDto registerUser(UserRegisterDto userRegisterDto) {
        log.trace(LogMessages.USER_ADD, userRegisterDto);

        User user = User.builder()
                .login(userRegisterDto.getLogin())
                .email(userRegisterDto.getEmail())
                .birthday(userRegisterDto.getBirthday())
                .name(userRegisterDto.getName() == null || userRegisterDto.getName().isBlank() ? userRegisterDto.getLogin() : userRegisterDto.getName())
                .build();

        log.debug(LogMessages.USER_SAVE_STARTED, user);
        User registeredUser = userRepository.insert(user);
        log.info(LogMessages.USER_SAVE_SUCCESS, registeredUser);
        return UserMapper.mapToUserDto(registeredUser);
    }

    public UserDto getUser(Long id) {
        User user = entityValidator.validateUserExists(id);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findMany().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        log.trace(LogMessages.USER_UPDATE, userUpdateDto);
        User user = entityValidator.validateUserExists(id);

        log.debug(LogMessages.USER_UPDATE_STARTED, id);
        user.setName(userUpdateDto.getName());
        user.setLogin(userUpdateDto.getLogin());
        user.setBirthday(userUpdateDto.getBirthday());
        user.setEmail(userUpdateDto.getEmail());

        int updatedRows = userRepository.update(user);
        log.info(LogMessages.USER_UPDATE_SUCCESS, user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public void deleteUser(Long id) {
        log.trace(LogMessages.USER_DELETE, id);
        entityValidator.validateUserExists(id);

        log.debug(LogMessages.USER_DELETE_STARTED, id);
        userRepository.deleteOneById(id);
        log.info(LogMessages.USER_DELETE_SUCCESS, id);
    }

    public List<UserDto> getUserFriends(Long id) {
        List<Long> friendIds = friendsRepository.findFriendsById(id);
        List<User> friends = userRepository.findManyByIds(friendIds);
        return friends.stream().map(UserMapper::mapToUserDto).toList();
    }

    public List<UserDto> getMutualFriends(Long id, Long otherId) {
        Set<Long> friendIdsUser = new HashSet<>(friendsRepository.findFriendsById(id));
        Set<Long> friendIdsOtherUser = new HashSet<>(friendsRepository.findFriendsById(otherId));

        Set<Long> mutualFriendIds = friendIdsUser.stream()
                .filter(friendIdsOtherUser::contains)
                .collect(Collectors.toSet());
        return userRepository.findManyByIds(mutualFriendIds.stream().toList())
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public void sendUserFriendRequest(Long userId, Long friendId) {
        entityValidator.validateUserExists(userId);
        entityValidator.validateUserExists(friendId);

        friendsRepository.sendFriendship(userId, friendId);
    }

    public void acceptUserFriendRequest(Long userId, Long friendId) {
        entityValidator.validateUserExists(userId);
        entityValidator.validateUserExists(friendId);

        friendsRepository.acceptFriendship(userId, friendId);
    }

    public void deleteUserFriend(Long userId, Long friendId) {
        entityValidator.validateUserExists(userId);
        entityValidator.validateUserExists(friendId);

        List<Long> friendsIds = friendsRepository.findFriendsById(userId);
        entityValidator.validateFriendExists(friendsIds, friendId);

        friendsRepository.deleteFriendship(userId, friendId);
    }
}
