package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public Film addFilm(Film film) {
        log.trace("Начало метода addFilm, на входе фильм: {}", film);

        // name validator
        String name = film.getName();
        if (name == null || name.isBlank()) {
            log.warn("Провал валидации поля name: пустое.");
            throw new FilmValidationException("Название фильма не может быть пустым.");
        }

        // description validator
        String description = film.getDescription();
        if (description.length() > 200) {
            log.warn("Провал валидации поля description: длиннее 200 символов.");
            throw new FilmValidationException("Описание фильма не может быть больше 200 символов.");
        }

        // releaseDate validator
        LocalDate releaseDate = film.getReleaseDate();
        LocalDate birthdayOfFilms = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(birthdayOfFilms)) {
            log.warn("Провал валидации поля releaseDate: вышел раньше первого фильма.");
            throw new FilmValidationException("Дата выхода фильма не может быть раньше дня рождения кино (28.12.1985).");
        }

        // duration validator
        Duration duration = film.getDuration();
        if (duration.isNegative() || duration.isZero()) {
            log.warn("Провал валидации поля duration: меньше или равно нулю.");
            throw new FilmValidationException("Длительность фильма должна быть больше нуля.");
        }

        log.debug("Начинается сохранение фильма: {}", film);
        Film savedFilm = filmRepository.save(film);
        log.info("Фильм успешно сохранен и возвращен: {}", savedFilm);
        return savedFilm;
    }

    public Film getFilm(Long id) {
        Film film = filmRepository.findById(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id: " + id + " не существует.");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film updateFilm(Film filmToUpdate) {
        log.trace("Начало метода updateFilm, на входе фильм: {}", filmToUpdate);
        Long id = filmToUpdate.getId();
        Film film = filmRepository.findById(id);
        if (film == null) {
            log.warn("Попытка обновить данные фильма, фильм не найден, id: {}", id);
            throw new FilmNotFoundException("Фильм с id: " + id + " не существует.");
        }
        log.debug("Начинается обновление фильма: {}", filmToUpdate);
        Film updatedFilm = filmRepository.update(filmToUpdate);
        log.info("Фильм успешно обновлен и возвращен: {}", updatedFilm);
        return updatedFilm;
    }

    public void deleteFilm(Long id) {
        log.trace("Начало метода deleteFilm, на входе фильм с id: {}", id);
        Film film = filmRepository.findById(id);
        if (film == null) {
            log.warn("Попытка удалить фильм, фильм не найден, id: {}", id);
            throw new FilmNotFoundException("Фильм с id: " + id + " не существует.");
        }
        log.debug("Начинается удаление фильма с id: {}", id);
        Film deletedFilm = filmRepository.deleteById(id);
        log.info("Фильм успешно удален и возвращен: {}", deletedFilm);
    }
}
