package ru.yandex.practicum.filmorate.repository.base;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
public class JdbcBaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected T findOne(String query, Object... params) {
        List<T> results = jdbc.query(query, mapper, params);
        return results.isEmpty() ? null : results.get(0);
    }

    protected List<T> findMany(String query) {
        return jdbc.query(query, mapper);
    }

    protected List<T> findManyByIds(String query, List<Long> ids) {
        String placeholders = String.join(",", ids.stream().map(id -> "?").toArray(String[]::new));
        String fullQuery = query + " (" + placeholders + ")";

        Object[] params = ids.toArray();

        return jdbc.query(fullQuery, mapper, params);
    }

    protected int delete(String query, Long id) {
        return jdbc.update(query, id);
    }

    protected int update(String query, Object... params) {
        return jdbc.update(query, params);
    }

    protected Long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }

    protected void cleanup(String query) {
        jdbc.execute(query);
    }
}
