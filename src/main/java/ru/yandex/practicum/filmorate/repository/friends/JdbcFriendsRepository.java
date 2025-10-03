package ru.yandex.practicum.filmorate.repository.friends;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;

import java.util.List;

@Repository("jdbcFriendsRepository")
@Profile("dev")
public class JdbcFriendsRepository extends JdbcBaseRepository<Friendship> implements FriendsRepository {
    public static final String INSERT_QUERY = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    public static final String DELETE_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    public static final String FIND_FRIENDSHIP_QUERY = "SELECT user_id, friend_id FROM friendships WHERE user_id = ? AND friend_id = ?";
    public static final String FIND_FRIENDS_BY_ID_QUERY = "SELECT friend_id FROM friendships WHERE user_id = ? AND friendship_status = 2";

    public JdbcFriendsRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addFriendship(Long userId, Long friendId) {
        update(INSERT_QUERY,
                userId,
                friendId);
    }

    @Override
    public void removeFriendship(Long userId, Long friendId) {
        update(DELETE_QUERY,
                userId,
                friendId);
    }

    @Override
    @Nullable
    public Friendship findFriendship(Long userId, Long friendId) {
        List<Friendship> result = jdbc.query(FIND_FRIENDSHIP_QUERY, mapper, userId, friendId);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Friendship> findFriendshipsByUserId(Long userId) {
        String query = """
                SELECT user_id, friend_id
                FROM friendships
                WHERE user_id = ?
                """;
        return jdbc.query(query, mapper, userId);
    }

    @Override
    public void cleanup() {
        jdbc.update("DELETE FROM FRIENDSHIPS");
    }
}
