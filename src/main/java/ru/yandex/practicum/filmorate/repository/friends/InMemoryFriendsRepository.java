package ru.yandex.practicum.filmorate.repository.friends;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("inMemoryFriendsRepository")
public class InMemoryFriendsRepository implements FriendsRepository {
    private final Map<Long, Map<Long, Boolean>> friendships = new HashMap<>();

    @Override
    public void sendFriendship(Long userId, Long friendId) {
        friendships.computeIfAbsent(userId, k -> new HashMap<>()).put(friendId, true);
        friendships.computeIfAbsent(friendId, k -> new HashMap<>()).put(userId, false);
    }

    @Override
    public void acceptFriendship(Long userId, Long friendId) {
        Map<Long, Boolean> friends = friendships.get(userId);
        friends.put(friendId, true);
    }

    @Override
    public void deleteFriendship(Long userId, Long friendId) {
        friendships.get(userId).remove(friendId);
        friendships.get(friendId).remove(userId);
    }

    @Override
    public List<Friendship> findFriendshipsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void cleanup() {
        friendships.clear();
    }
}
