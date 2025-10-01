package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    User insert(User user);

    User findOneById(Long id);

    List<User> findManyByIds(List<Long> ids);

    List<User> findMany();

    int update(User user);

    void deleteOneById(Long id);

    void cleanup();
}
