package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmRegisterDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.logging.LogMessages;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
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
}
