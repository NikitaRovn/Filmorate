package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipId;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {

    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        FriendshipId id = new FriendshipId();
        id.setUserId(rs.getLong("user_id"));
        id.setFriendId(rs.getLong("friend_id"));

        return Friendship.builder()
                .id(id)
                .user(User.builder().id(rs.getLong("user_id")).build())
                .friend(User.builder().id(rs.getLong("friend_id")).build())
                .build();
    }
}
