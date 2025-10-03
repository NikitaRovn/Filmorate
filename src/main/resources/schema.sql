CREATE TABLE IF NOT EXISTS MPA_RATINGS
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL
        CONSTRAINT unique_name UNIQUE,
    description   TEXT         NOT NULL,
    release_date  DATE         NOT NULL,
    duration      INT          NOT NULL,
    mpa_rating_id BIGINT       NOT NULL,
    FOREIGN KEY (mpa_rating_id) REFERENCES mpa_ratings (id)
);

CREATE INDEX IF NOT EXISTS idx_films_mpa_rating ON films (mpa_rating_id);

CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR(255) NOT NULL
        CONSTRAINT unique_email UNIQUE,
    login    VARCHAR(255) NOT NULL
        CONSTRAINT unique_login UNIQUE,
    name     VARCHAR(255) NOT NULL,
    birthday DATE         NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id    BIGINT NOT NULL,
    genre_id   BIGINT NOT NULL,
    sort_order INT    NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (genre_id) REFERENCES genres (id)
);

CREATE INDEX IF NOT EXISTS idx_film_genres ON film_genres (genre_id);

CREATE TABLE IF NOT EXISTS user_film_likes
(
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_user_film_likes ON user_film_likes (user_id);

CREATE TABLE IF NOT EXISTS friendships
(
    user_id           BIGINT  NOT NULL,
    friend_id         BIGINT  NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

