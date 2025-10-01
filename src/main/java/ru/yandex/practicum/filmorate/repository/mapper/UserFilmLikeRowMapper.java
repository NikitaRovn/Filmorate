package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFilmLike;
import ru.yandex.practicum.filmorate.model.UserFilmLikeId;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFilmLikeRowMapper implements RowMapper<UserFilmLike> {
    @Override
    public UserFilmLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("user_id");
        Long filmId = rs.getLong("film_id");

        return UserFilmLike.builder()
                .id(new UserFilmLikeId() {{
                    setUserId(userId);
                    setFilmId(filmId);
                }})
                .user(User.builder().id(userId).build())
                .film(Film.builder().id(filmId).build())
                .build();
    }
}
