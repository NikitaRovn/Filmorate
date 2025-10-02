package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository("inMemoryFilmRepository")
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
    public Film findOneById(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public int update(Film film) {
        Long id = film.getId();
        films.put(id, film);
        return 0;
    }

    @Override
    public int deleteOneById(Long id) {
        return 0;
    }

    @Override
    public void cleanup() {
        films.clear();
        lastId = 1L;
    }
}
