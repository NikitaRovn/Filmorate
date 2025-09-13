package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepository {
    Film save(Film film);

    Film findById(Long id);

    List<Film> findAll();

    Film update(Film film);

    Film deleteById(Long id);

    void clear();
}