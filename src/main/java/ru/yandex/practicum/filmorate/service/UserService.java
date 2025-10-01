package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserRegisterDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.logging.LogMessages;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friends.FriendsRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    public UserService(@Qualifier("jdbcUserRepository") UserRepository userRepository, @Qualifier("jdbcFriendsRepository") FriendsRepository friendsRepository) {
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
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
        User user = userRepository.findOneById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findMany().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        log.trace(LogMessages.USER_UPDATE, userUpdateDto);
        User user = userRepository.findOneById(id);
        if (user == null) {
            log.warn(LogMessages.USER_UPDATE_NOT_FOUND, id);
            throw new UserNotFoundException(id);
        }
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
        User user = userRepository.findOneById(id);
        if (user == null) {
            log.warn(LogMessages.USER_DELETE_NOT_FOUND, id);
            throw new UserNotFoundException(id);
        }
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
        List<Long> friendIdsUser = friendsRepository.findFriendsById(id);
        List<User> friendsUser = userRepository.findManyByIds(friendIdsUser);
        List<Long> friendIdsOtherUser = friendsRepository.findFriendsById(otherId);

        Set<Long> otherIds = new HashSet<>(friendIdsOtherUser);

        List<User> listMutualFriends = friendsUser.stream()
                .filter(user -> otherIds.contains(user.getId()))
                .toList();

        return listMutualFriends.stream().map(UserMapper::mapToUserDto).toList();
    }

    public void sendUserFriendRequest(Long userId, Long friendId) {
        friendsRepository.sendFriendship(userId, friendId);
    }

    public void acceptUserFriendRequest(Long id, Long friendId) {
        friendsRepository.acceptFriendship(id, friendId);
    }

    public void deleteUserFriend(Long id, Long friendId) {
        List<Long> friends = friendsRepository.findFriendsById(id);

        if (friends == null) {
            throw new UserNotFoundException(id);
        } else if (!friends.contains(friendId)) {
            throw new FriendNotFoundException(friendId);
        }

        friendsRepository.deleteFriendship(id, friendId);
    }
}
