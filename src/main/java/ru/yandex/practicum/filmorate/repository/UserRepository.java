package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long lastId = 1L;

    public User save(User user) {
        user.setId(lastId);
        users.put(lastId, user);
        lastId++;
        return user;
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User update(User user) {
        Long id = user.getId();
        users.put(id, user);
        return users.get(id);
    }

    public User deleteById(Long id) {
        return users.remove(id);
    }

    public void clear() {
        users.clear();
        lastId = 1L;
    }
}
