package ru.yandex.practicum.filmorate.repository.friends;

import java.util.List;

public interface FriendsRepository {
    void sendFriendship(Long userId, Long friendId);

    void acceptFriendship(Long userId, Long friendId);

    void deleteFriendship(Long userId, Long friendId);

    List<Long> findFriendsById(Long id);
}
