package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Repository("jdbcGenreRepository")
@Profile("dev")
public class JdbcGenreRepository extends JdbcBaseRepository<Genre> implements GenreRepository {
    public static final String FIND_ALL_QUERY = "SELECT * FROM GENRES";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRES WHERE ID = ?";
    private static final String FIND_BY_IDS = "SELECT id, name FROM genres WHERE id IN (%s)";

    public JdbcGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public List<Genre> findManyByIds(List<Long> ids) {
        if (ids.isEmpty()) return List.of();
        String in = ids.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        return jdbc.query(String.format(FIND_BY_IDS, in), mapper);
    }

    @Override
    public Genre findOneById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
