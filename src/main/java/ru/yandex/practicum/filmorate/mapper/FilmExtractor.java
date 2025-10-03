package ru.yandex.practicum.filmorate.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.MpaRatingRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@AllArgsConstructor
public class FilmExtractor implements ResultSetExtractor<Film> {
    private final MpaRatingRowMapper mpaRatingRowMapper;
    private final GenreRowMapper genreRowMapper;

    @Override
    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.next()) {
            return null;
        }

        MpaRating mpaRating = rs.getObject("mpa_id") == null ? null : mpaRatingRowMapper.mapRow(rs, 0);

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpaRating(mpaRating)
                .genres(new ArrayList<>())
                .build();

        List<Genre> genres = film.getGenres();

        do {
            Long genreId = rs.getObject("genre_id", Long.class);
            String genreName = rs.getString("genre_name");
            if (genreId != null) {
                genres.add(new Genre(genreId, genreName));
            }
        } while (rs.next());

        return film;
    }
}
