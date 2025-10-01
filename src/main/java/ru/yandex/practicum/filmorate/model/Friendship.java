package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.EmbeddedId;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Friendship {
    @EmbeddedId
    private FriendshipId id;

    User user;
    User friend;
    FriendshipStatus friendshipStatus;
}
