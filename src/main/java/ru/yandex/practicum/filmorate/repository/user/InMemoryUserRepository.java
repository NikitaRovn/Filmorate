package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.context.annotation.Profile;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Profile("dev")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long lastId = 1L;

    @Override
    public User save(User user) {
        user.setId(lastId);
        users.put(lastId, user);
        lastId++;
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User deleteById(Long id) {
        return users.remove(id);
    }

    @Override
    public void clear() {
        users.clear();
        lastId = 1L;
    }
}
