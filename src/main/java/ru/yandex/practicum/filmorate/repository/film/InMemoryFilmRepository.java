package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Profile("dev")
public class InMemoryFilmRepository implements FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();
    private Long lastId = 1L;

    @Override
    public Film save(Film film) {
        film.setId(lastId);
        films.put(lastId, film);
        lastId++;
        return film;
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film update(Film film) {
        Long id = film.getId();
        films.put(id, film);
        return films.get(id);
    }

    @Override
    public Film deleteById(Long id) {
        return films.remove(id);
    }

    @Override
    public void clear() {
        films.clear();
        lastId = 1L;
    }
}
