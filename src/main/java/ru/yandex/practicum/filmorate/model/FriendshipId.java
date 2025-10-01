package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class FriendshipId implements Serializable {
    private Long userId;
    private Long friendId;
}
