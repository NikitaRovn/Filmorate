package ru.yandex.practicum.filmorate.repository.mpa_rating;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;

import java.util.List;

@Repository("jdbcMpaRatingRepository")
@Profile("dev")
public class JdbcMpaRatingRepository extends JdbcBaseRepository<MpaRating> implements MpaRatingRepository {
    public static final String FIND_ALL_QUERY = "SELECT id AS mpa_id, name AS mpa_name FROM MPA_RATINGS";
    public static final String FIND_BY_ID_QUERY = "SELECT id AS mpa_id, name AS mpa_name FROM MPA_RATINGS WHERE ID = ?";

    public JdbcMpaRatingRepository(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public MpaRating findOneById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
