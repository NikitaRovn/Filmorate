package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User findById(Long id);

    List<User> findAll();

    User update(User user);

    User deleteById(Long id);

    void clear();
}
