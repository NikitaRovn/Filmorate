package ru.yandex.practicum.filmorate.repository.likes;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Profile("dev")
public class InMemoryLikesRepository implements LikesRepository {
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public void addLike(Long filmId, Long userId) {
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likes.get(filmId).remove(userId);
    }

    @Override
    public Set<Long> findLikesByFilmId(Long filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet());
    }

    @Override
    public List<Long> findTopFilmsByLikes(Integer count) {
        return likes.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<Long, Set<Long>> e) -> e.getValue().size()).reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void clear() {
        likes.clear();
    }
}
