package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.mpa_rating.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa_rating.MpaRatingRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;
import java.util.List;

@Component
public class EntityValidator {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public EntityValidator(
            @Qualifier("jdbcUserRepository") UserRepository userRepository,
            @Qualifier("jdbcFilmRepository") FilmRepository filmRepository,
            @Qualifier("jdbcMpaRatingRepository") MpaRatingRepository mpaRatingRepository,
            @Qualifier("jdbcGenreRepository") GenreRepository genreRepository) {
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.mpaRatingRepository = mpaRatingRepository;
        this.genreRepository = genreRepository;
    }

    public User validateUserExists(Long id) {
        User user = userRepository.findOneById(id);
        if (user == null) throw new UserNotFoundException(id);
        return user;
    }

    public void validateFriendExists(Collection<Long> friendsIds, Long friendId) {
        if (!friendsIds.contains(friendId)) {
            throw new FriendNotFoundException(friendId);
        }
    }

    public Film validateFilmExists(Long id) {
        Film film = filmRepository.findOneById(id);
        if (film == null) throw new FilmNotFoundException(id);
        return film;
    }

    public Genre validateGenreExists(Long id) {
        Genre genre = genreRepository.findOneById(id);
        if (genre == null) throw new GenreNotFoundException(id);
        return genre;
    }

    public MpaRating validateMpaRatingExists(Long id) {
        MpaRating mpaRating = mpaRatingRepository.findOneById(id);
        if (mpaRating == null) throw new MpaRatingNotFoundException(id);
        return mpaRating;
    }
}
