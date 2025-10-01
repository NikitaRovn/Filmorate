package ru.yandex.practicum.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilmListExtractor implements ResultSetExtractor<List<Film>> {
    private final FilmExtractor singleExtractor;

    public FilmListExtractor(FilmExtractor singleExtractor) {
        this.singleExtractor = singleExtractor;
    }

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> filmsMap = new LinkedHashMap<>();

        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            Film film = filmsMap.get(filmId);

            if (film == null) {
                film = Film.builder()
                        .id(filmId)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpaRating(singleExtractor.getMpaRatingRowMapper().mapRow(rs, 0))
                        .genres(new ArrayList<>())
                        .build();
                filmsMap.put(filmId, film);
            }

            if (rs.getObject("genre_id") != null) {
                Genre genre = singleExtractor.getGenreRowMapper().mapRow(rs, 0);
                film.getGenres().add(genre);
            }
        }

        return new ArrayList<>(filmsMap.values());
    }
}
