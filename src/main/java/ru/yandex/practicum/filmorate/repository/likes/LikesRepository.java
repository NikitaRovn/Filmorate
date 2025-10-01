package ru.yandex.practicum.filmorate.repository.likes;

import java.util.List;
import java.util.Set;

public interface LikesRepository {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Set<Long> findLikesByFilmId(Long filmId);

    List<Long> findTopFilmsByLikes(Integer count);

    void cleanup();
}
