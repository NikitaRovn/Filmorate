package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;

import java.util.List;

@Repository("jdbcUserRepository")
@Profile("dev")
public class JdbcUserRepository extends JdbcBaseRepository<User> implements UserRepository {
    public static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    public static final String FIND_BY_IDS_QUERY = "SELECT * FROM users WHERE id IN";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE id = ?";
    public static final String INSERT_QUERY = "INSERT INTO USERS(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    public static final String DELETE_QUERY = "DELETE FROM USERS WHERE id = ?";
    public static final String CLEANUP_QUERY = "DELETE FROM USERS";

    public JdbcUserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User insert(User user) {
        Long id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User findOneById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<User> findManyByIds(List<Long> ids) {
        return findManyByIds(FIND_BY_IDS_QUERY, ids);
    }

    @Override
    public List<User> findMany() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public int update(User user) {
        return update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public void deleteOneById(Long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void cleanup() {
        cleanup(CLEANUP_QUERY);
    }
}
