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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        saveFilmGenres(id, film.getGenres());

        film.setGenres(loadGenresForFilms(List.of(id)).getOrDefault(id, List.of()));
        return film;
    }

    @Override
    public Film findOneById(Long id) {
        Film film = jdbc.query(FIND_BY_ID_QUERY, filmExtractor, id);
        if (film == null) return null;
        film.setGenres(loadGenresForFilms(List.of(id)).getOrDefault(id, List.of()));
        return film;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbc.query(FIND_ALL_QUERY, new FilmListExtractor(filmExtractor));
        if (films.isEmpty()) return films;

        Map<Long, List<Genre>> genreMap = loadGenresForFilms(
                films.stream().map(Film::getId).toList());
        films.forEach(f -> f.setGenres(genreMap.getOrDefault(f.getId(), List.of())));
        return films;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId());

        saveFilmGenres(film.getId(), film.getGenres());

        film.setGenres(loadGenresForFilms(List.of(film.getId()))
                .getOrDefault(film.getId(), List.of()));

        return film;
    }

    @Override
    public int deleteOneById(Long filmId) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        return jdbc.update("DELETE FROM films WHERE id = ?", filmId);
    }

    @Override
    public void cleanup() {

    }

    void saveFilmGenres(Long filmId, List<Genre> genres) {
        genres = (genres == null) ? List.of() : genres;

        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        String sql = "INSERT INTO film_genres(film_id, genre_id, sort_order) VALUES (?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        int order = 1;
        for (Genre g : genres) {
            batch.add(new Object[]{filmId, g.getId(), order++});
        }
        jdbc.batchUpdate(sql, batch);
    }

    Map<Long, List<Genre>> loadGenresForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) return Map.of();
        String in = filmIds.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        String sql = """
        SELECT fg.film_id, g.id, g.name
        FROM film_genres fg
        JOIN genres g ON fg.genre_id = g.id
        WHERE fg.film_id IN (%s)
        ORDER BY fg.film_id, fg.sort_order
        """.formatted(in);

        Map<Long, List<Genre>> map = new LinkedHashMap<>();
        jdbc.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            map.computeIfAbsent(filmId, k -> new ArrayList<>())
                    .add(Genre.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .build());
        });
        return map;
    }
}
