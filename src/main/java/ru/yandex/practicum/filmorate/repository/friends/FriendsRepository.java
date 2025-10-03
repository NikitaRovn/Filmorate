package ru.yandex.practicum.filmorate.repository.friends;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendsRepository {
    void addFriendship(Long userId, Long friendId);

    void removeFriendship(Long userId, Long friendId);

    Friendship findFriendship(Long userId, Long friendId);

    List<Friendship> findFriendshipsByUserId(Long userId);

    void cleanup();
}
