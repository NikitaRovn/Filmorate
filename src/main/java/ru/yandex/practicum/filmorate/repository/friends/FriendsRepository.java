package ru.yandex.practicum.filmorate.repository.friends;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendsRepository {
    void sendFriendship(Long userId, Long friendId);

    void acceptFriendship(Long userId, Long friendId);

    void deleteFriendship(Long userId, Long friendId);

    Friendship findFriendship(Long userId, Long friendId);

    List<Friendship> findFriendshipsByUserId(Long userId);

    void cleanup();
}
