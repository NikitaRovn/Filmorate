package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmRegisterDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.logging.LogMessages;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.InMemoryFilmRepository;
import ru.yandex.practicum.filmorate.repository.likes.InMemoryLikesRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmRepository filmRepository;
    private final InMemoryLikesRepository likesRepository;

    public FilmService(InMemoryFilmRepository filmRepository, InMemoryLikesRepository likesRepository) {
        this.filmRepository = filmRepository;
        this.likesRepository = likesRepository;
    }

    public Film addFilm(FilmRegisterDto filmRegisterDto) {
        log.trace(LogMessages.FILM_ADD, filmRegisterDto);

        Film film = Film.builder()
                .name(filmRegisterDto.getName())
                .description(filmRegisterDto.getDescription())
                .releaseDate(filmRegisterDto.getReleaseDate())
                .duration(filmRegisterDto.getDuration())
                .build();

        log.debug(LogMessages.FILM_SAVE_STARTED, film);
        Film savedFilm = filmRepository.save(film);
        log.info(LogMessages.FILM_SAVE_SUCCESS, savedFilm);
        return savedFilm;
    }

    public Film getFilm(Long id) {
        Film film = filmRepository.findById(id);
        if (film == null) {
            throw new FilmNotFoundException(id);
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film updateFilm(FilmUpdateDto filmUpdateDto, Long id) {
        log.trace(LogMessages.FILM_UPDATE, filmUpdateDto);
        Film film = filmRepository.findById(id);
        if (film == null) {
            log.warn(LogMessages.FILM_UPDATE_NOT_FOUND, id);
            throw new FilmNotFoundException(id);
        }
        log.debug(LogMessages.FILM_UPDATE_STARTED, id);
        film.setName(filmUpdateDto.getName());
        film.setDescription(filmUpdateDto.getDescription());
        film.setReleaseDate(filmUpdateDto.getReleaseDate());
        film.setDuration(filmUpdateDto.getDuration());

        Film updatedFilm = filmRepository.update(film);
        log.info(LogMessages.FILM_UPDATE_SUCCESS, updatedFilm);
        return updatedFilm;
    }

    public void deleteFilm(Long id) {
        log.trace(LogMessages.FILM_DELETE, id);
        Film film = filmRepository.findById(id);
        if (film == null) {
            log.warn(LogMessages.FILM_DELETE_NOT_FOUND, id);
            throw new FilmNotFoundException(id);
        }
        log.debug(LogMessages.FILM_DELETE_STARTED, id);
        Film deletedFilm = filmRepository.deleteById(id);
        log.info(LogMessages.FILM_DELETE_SUCCESS, deletedFilm);
    }

    public List<Film> getMostLikedFilms(Integer count) {
        if (count == null || count <= 0) {
            count = 10;
        }

        List<Long> mostLikedFilms = likesRepository.findTopFilmsByLikes(count);
        return mostLikedFilms.stream().map(this::getFilm).toList();
    }

    public Set<Long> addLike(Long id, Long userId) {
        likesRepository.addLike(id, userId);
        return likesRepository.findLikesByFilmId(id);
    }

    public Set<Long> deleteLike(Long id, Long userId) {
        likesRepository.deleteLike(id, userId);
        return likesRepository.findLikesByFilmId(id);
    }
}
