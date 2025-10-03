package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {
    List<Genre> findAll();

    Genre findOneById(Long id);

    List<Genre> findManyByIds(List<Long> ids);
}
