package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.mpa_rating.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.mpa_rating.MpaRatingRepository;

import java.util.List;

@Service
@Slf4j
public class MpaRatingService {
    private final MpaRatingRepository mpaRatingRepository;

    public MpaRatingService(@Qualifier("jdbcMpaRatingRepository") MpaRatingRepository mpaRatingRepository) {
        this.mpaRatingRepository = mpaRatingRepository;
    }

    public List<MpaRatingDto> getAllMpaRatings() {
        return mpaRatingRepository.findAll().stream()
                .map(MpaRatingMapper::mapToMpaRatingDto)
                .toList();
    }

    public MpaRatingDto getRating(Long id) {
        MpaRating mpaRating = mpaRatingRepository.findOneById(id);
        if (mpaRating == null) {
            throw new MpaRatingNotFoundException(id);
        }
        return MpaRatingMapper.mapToMpaRatingDto(mpaRating);
    }
}
