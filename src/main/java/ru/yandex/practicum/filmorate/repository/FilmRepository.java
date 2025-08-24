package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();
    private Long lastId = 1L;

    public Film save(Film film) {
        film.setId(lastId);
        films.put(lastId, film);
        lastId++;
        return film;
    }

    public Film findById(Long id) {
        return films.get(id);
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film update(Film film) {
        Long id = film.getId();
        films.put(id, film);
        return films.get(id);
    }

    public Film deleteById(Long id) {
        return films.remove(id);
    }

    public void clear() {
        films.clear();
        lastId = 1L;
    }
}
