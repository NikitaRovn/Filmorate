package ru.yandex.practicum.filmorate.repository.likes;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserFilmLike;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("jdbcLikesRepository")
@Profile("dev")
public class JdbcLikesRepository extends JdbcBaseRepository<UserFilmLike> implements LikesRepository {
    public static final String INSERT_LIKE_QUERY = "INSERT INTO user_film_likes(film_id, user_id) VALUES (?, ?)";
    public static final String DELETE_LIKE_QUERY = "DELETE FROM user_film_likes WHERE film_id = ? AND user_id = ?";
    public static final String FIND_BY_ID_QUERY = "SELECT user_id FROM user_film_likes WHERE film_id = ?";
    public static final String FIND_LIKE_QUERY = "SELECT user_id, film_id FROM user_film_likes WHERE film_id = ? AND user_id = ?";

    public JdbcLikesRepository(JdbcTemplate jdbc, RowMapper<UserFilmLike> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        update(INSERT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    @Nullable
    public UserFilmLike findLike(Long filmId, Long userId) {
        List<UserFilmLike> result = jdbc.query(FIND_LIKE_QUERY, mapper, filmId, userId);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public Set<Long> findLikesByFilmId(Long filmId) {
        List<Long> userIds = jdbc.queryForList(FIND_BY_ID_QUERY, Long.class, filmId);
        return userIds.stream().collect(Collectors.toSet());
    }

    @Override
    public List<Long> findTopFilmsByLikes(Integer count) {
        String sql = """
                SELECT film_id
                FROM user_film_likes
                GROUP BY film_id
                ORDER BY COUNT(user_id) DESC
                LIMIT ?
                """;
        List<Long> result = jdbc.query(sql, (rs, rowNum) -> rs.getLong("film_id"), count);
        System.out.println(">>> FOUND FILM IDs: " + result);
        return result;
    }

    @Override
    public void cleanup() {
        jdbc.update("DELETE FROM FRIENDSHIPS");
    }
}
