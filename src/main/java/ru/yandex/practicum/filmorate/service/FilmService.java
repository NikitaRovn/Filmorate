package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmRegisterDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
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
        log.trace("Начало метода addFilm, на входе фильм: {}", filmRegisterDto);

//        // releaseDate validator
//        LocalDate releaseDate = filmRegisterDto.getReleaseDate();
//        LocalDate birthdayOfFilms = LocalDate.of(1895, 12, 28);
//        if (releaseDate.isBefore(birthdayOfFilms)) {
//            log.warn("Провал валидации поля releaseDate: вышел раньше первого фильма.");
//            throw new FilmValidationException("Дата выхода фильма не может быть раньше дня рождения кино (28.12.1985).");
//        }

//        // duration validator
//        Duration duration = filmRegisterDto.getDuration();
//        if (duration.isNegative() || duration.isZero()) {
//            log.warn("Провал валидации поля duration: меньше или равно нулю.");
//            throw new FilmValidationException("Длительность фильма должна быть больше нуля.");
//        }

        Film film = Film.builder()
                .name(filmRegisterDto.getName())
                .description(filmRegisterDto.getDescription())
                .releaseDate(filmRegisterDto.getReleaseDate())
                .duration(filmRegisterDto.getDuration())
                .build();

        log.debug("Начинается сохранение фильма: {}", film);
        Film savedFilm = filmRepository.save(film);
        log.info("Фильм успешно сохранен и возвращен: {}", savedFilm);
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
        log.trace("Начало метода updateFilm, на входе фильм: {}", filmUpdateDto);
        Film film = filmRepository.findById(id);
        if (film == null) {
            log.warn("Попытка обновить данные фильма, фильм не найден, id: {}", id);
            throw new FilmNotFoundException(id);
        }
        log.debug("Начинается обновление фильма: {}, id: {}", filmUpdateDto, id);
        film.setName(filmUpdateDto.getName());
        film.setDescription(filmUpdateDto.getDescription());
        film.setReleaseDate(filmUpdateDto.getReleaseDate());
        film.setDuration(filmUpdateDto.getDuration());

        Film updatedFilm = filmRepository.update(film);
        log.info("Фильм успешно обновлен и возвращен: {}", updatedFilm);
        return updatedFilm;
    }

    public void deleteFilm(Long id) {
        log.trace("Начало метода deleteFilm, на входе фильм с id: {}", id);
        Film film = filmRepository.findById(id);
        if (film == null) {
            log.warn("Попытка удалить фильм, фильм не найден, id: {}", id);
            throw new FilmNotFoundException(id);
        }
        log.debug("Начинается удаление фильма с id: {}", id);
        Film deletedFilm = filmRepository.deleteById(id);
        log.info("Фильм успешно удален и возвращен: {}", deletedFilm);
    }
}
