package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository("inMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long lastId = 1L;

    @Override
    public User insert(User user) {
        user.setId(lastId);
        users.put(lastId, user);
        lastId++;
        return user;
    }

    @Override
    public User findOneById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> findManyByIds(List<Long> ids) {
        return ids.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<User> findMany() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int update(User user) {
        Long id = user.getId();
        users.put(id, user);
        return 0;
    }

    @Override
    public void deleteOneById(Long id) {
        users.remove(id);
    }

    @Override
    public void cleanup() {
        users.clear();
        lastId = 1L;
    }
}
