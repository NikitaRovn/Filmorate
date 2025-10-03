package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmGenreId;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreRowMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenreId id = new FilmGenreId();
        id.setFilmId(rs.getLong("film_id"));
        id.setGenreId(rs.getLong("genre_id"));

        Genre genre = Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();

        return FilmGenre.builder()
                .id(id)
                .genre(genre)
                .sortOrder(rs.getInt("sort_order"))
                .build();
    }
}