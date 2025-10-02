package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepository {
    Film save(Film film);

    Film findOneById(Long id);

    List<Film> findAll();

    int update(Film film);

    int deleteOneById(Long id);

    void cleanup();
}