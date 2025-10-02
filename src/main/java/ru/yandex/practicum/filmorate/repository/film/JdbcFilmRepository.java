package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmExtractor;
import ru.yandex.practicum.filmorate.mapper.FilmListExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.base.JdbcBaseRepository;


import java.util.ArrayList;
import java.util.List;

@Repository("jdbcFilmRepository")
@Profile("dev")
public class JdbcFilmRepository extends JdbcBaseRepository<Film> implements FilmRepository {
    private final FilmExtractor filmExtractor;

    public static final String FIND_ALL_QUERY = """
            SELECT f.id as film_id, f.name, f.description, f.release_date, f.duration,
            m.id as mpa_id, m.name as mpa_name,
            g.id as genre_id, g.name as genre_name,
            fg.sort_order
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            ORDER BY fg.sort_order
            """;
    public static final String FIND_BY_ID_QUERY = """
            SELECT f.id as film_id, f.name, f.description, f.release_date, f.duration,
            m.id as mpa_id, m.name as mpa_name,
            g.id as genre_id, g.name as genre_name,
            fg.sort_order
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            WHERE f.id = ?
            ORDER BY fg.sort_order
            """;
    public static final String INSERT_QUERY = """
            INSERT INTO FILMS(name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    public static final String UPDATE_QUERY = """
            UPDATE FILMS
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
            WHERE id = ?
            """;

    public JdbcFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmExtractor filmExtractor) {
        super(jdbc, mapper);
        this.filmExtractor = filmExtractor;
    }

    @Override
    public Film save(Film film) {
        Long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId());
        film.setId(id);

        String query = "INSERT INTO FILM_GENRES(film_id, genre_id, sort_order) VALUES (?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        int sortOrder = 1;
        for (Genre genre : film.getGenres()) {
            batch.add(new Object[]{film.getId(), genre.getId(), sortOrder++});
        }
        jdbc.batchUpdate(query, batch);

        return findOneById(film.getId());
    }

    @Override
    public Film findOneById(Long id) {
        return jdbc.query(FIND_BY_ID_QUERY, filmExtractor, id);
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, new FilmListExtractor(filmExtractor));
    }

    @Override
    public int update(Film film) {
        int updatedRows = update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId());

        String deleteGenresQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(deleteGenresQuery, film.getId());

        String insertGenreQuery = "INSERT INTO film_genres(film_id, genre_id, sort_order) VALUES (?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        int sortOrder = 1;
        for (Genre genre : film.getGenres()) {
            batch.add(new Object[]{film.getId(), genre.getId(), sortOrder++});
        }
        jdbc.batchUpdate(insertGenreQuery, batch);

        return updatedRows;
    }

    @Override
    public int deleteOneById(Long id) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", id);
        return jdbc.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public void cleanup() {

    }
}
