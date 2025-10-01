package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmRegisterDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.logging.LogMessages;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.likes.LikesRepository;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final LikesRepository likesRepository;

    public FilmService(@Qualifier("jdbcFilmRepository") FilmRepository filmRepository, @Qualifier("jdbcMemoryLikesRepository") LikesRepository likesRepository) {
        this.filmRepository = filmRepository;
        this.likesRepository = likesRepository;
    }

    public FilmDto addFilm(FilmRegisterDto filmRegisterDto) {
        log.trace(LogMessages.FILM_ADD, filmRegisterDto);

        Film film = Film.builder()
                .name(filmRegisterDto.getName())
                .description(filmRegisterDto.getDescription())
                .releaseDate(filmRegisterDto.getReleaseDate())
                .duration(filmRegisterDto.getDuration())
                .mpaRating(MpaRating.builder()
                        .id(filmRegisterDto.getMpaRatingId())
                        .build())
                .genres(filmRegisterDto.getGenresIds().stream()
                        .map(id -> Genre.builder()
                                .id(id)
                                .build())
                        .toList())
                .build();

        log.debug(LogMessages.FILM_SAVE_STARTED, film);
        Film savedFilm = filmRepository.save(film);
        log.info(LogMessages.FILM_SAVE_SUCCESS, savedFilm);
        return FilmMapper.mapToFilmDto(savedFilm);
    }

    public FilmDto getFilm(Long id) {
        Film film = filmRepository.findOneById(id);
        if (film == null) {
            throw new FilmNotFoundException(id);
        }
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getAllFilms() {
        return filmRepository.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto updateFilm(FilmUpdateDto filmUpdateDto, Long id) {
        log.trace(LogMessages.FILM_UPDATE, filmUpdateDto);
        Film film = filmRepository.findOneById(id);
        if (film == null) {
            log.warn(LogMessages.FILM_UPDATE_NOT_FOUND, id);
            throw new FilmNotFoundException(id);
        }
        log.debug(LogMessages.FILM_UPDATE_STARTED, id);
        film.setName(filmUpdateDto.getName());
        film.setDescription(filmUpdateDto.getDescription());
        film.setReleaseDate(filmUpdateDto.getReleaseDate());
        film.setDuration(filmUpdateDto.getDuration());

        filmRepository.update(film);
        log.info(LogMessages.FILM_UPDATE_SUCCESS, id);
        return FilmMapper.mapToFilmDto(film);
    }

    public void deleteFilm(Long id) {
        log.trace(LogMessages.FILM_DELETE, id);
        Film film = filmRepository.findOneById(id);
        if (film == null) {
            log.warn(LogMessages.FILM_DELETE_NOT_FOUND, id);
            throw new FilmNotFoundException(id);
        }
        log.debug(LogMessages.FILM_DELETE_STARTED, id);
        filmRepository.deleteOneById(id);
        log.info(LogMessages.FILM_DELETE_SUCCESS, id);
    }

    public List<FilmDto> getMostLikedFilms(Integer count) {
        if (count == null || count <= 0) {
            count = 10;
        }

        List<Long> mostLikedFilms = likesRepository.findTopFilmsByLikes(count);
        return mostLikedFilms.stream().map(this::getFilm).toList();
    }

    public void addLike(Long id, Long userId) {
        likesRepository.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        likesRepository.deleteLike(id, userId);
    }

    public Set<Long> getLikes(Long filmId) {
        return likesRepository.findLikesByFilmId(filmId);
    }
}
