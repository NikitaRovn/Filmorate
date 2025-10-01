package ru.yandex.practicum.filmorate.repository.mpa_rating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingRepository {
    List<MpaRating> findAll();

    MpaRating findOneById(Long id);
}
