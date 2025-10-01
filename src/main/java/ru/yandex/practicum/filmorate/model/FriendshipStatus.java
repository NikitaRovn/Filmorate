package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum FriendshipStatus {
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3);

    private final int code;

    FriendshipStatus(int code) {
        this.code = code;
    }

    public static FriendshipStatus fromCode(int code) {
        for (FriendshipStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}

