package ru.yandex.practicum.filmorate.repository.likes;

import ru.yandex.practicum.filmorate.model.UserFilmLike;

import java.util.List;
import java.util.Set;

public interface LikesRepository {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    UserFilmLike findLike(Long userId, Long filmId);

    Set<Long> findLikesByFilmId(Long filmId);

    List<Long> findTopFilmsByLikes(Integer count);

    void cleanup();
}
