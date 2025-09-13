package ru.yandex.practicum.filmorate.repository.friends;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Profile("dev")
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
    public List<Long> findFriendsById(Long id) {
        return friendships.getOrDefault(id, Map.of())
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }
}
