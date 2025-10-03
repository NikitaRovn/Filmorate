package ru.yandex.practicum.filmorate.repository.film_genre;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;

import java.util.List;

@Repository("jdbcFilmGenreRepository")
@Profile("dev")
public class JdbcFilmGenreRepository extends JdbcBaseRepository<FilmGenre> implements FilmGenreRepository {
    public JdbcFilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<FilmGenre> findGenresByFilmId(Long filmId) {
        String query = """
        SELECT fg.film_id, fg.genre_id, fg.sort_order, g.name AS genre_name
        FROM film_genres fg
        JOIN genres g ON fg.genre_id = g.id
        WHERE fg.film_id = ?
        ORDER BY fg.sort_order
        """;

        return jdbc.query(query, mapper, filmId);
    }
}
