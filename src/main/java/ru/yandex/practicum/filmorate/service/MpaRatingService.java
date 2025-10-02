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
    private final EntityValidator entityValidator;

    public MpaRatingService(@Qualifier("jdbcMpaRatingRepository") MpaRatingRepository mpaRatingRepository,
                            EntityValidator entityValidator) {
        this.mpaRatingRepository = mpaRatingRepository;
        this.entityValidator = entityValidator;
    }

    public List<MpaRatingDto> getAllMpaRatings() {
        return mpaRatingRepository.findAll().stream()
                .map(MpaRatingMapper::mapToMpaRatingDto)
                .toList();
    }

    public MpaRatingDto getRating(Long id) {
        MpaRating mpaRating = entityValidator.validateMpaRatingExists(id);
        return MpaRatingMapper.mapToMpaRatingDto(mpaRating);
    }
}
