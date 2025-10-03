package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenreService {
    private final GenreRepository genreRepository;
    private final EntityValidator entityValidator;

    public GenreService(@Qualifier("jdbcGenreRepository") GenreRepository genreRepository,
                        EntityValidator entityValidator) {
        this.genreRepository = genreRepository;
        this.entityValidator = entityValidator;
    }

    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenre(Long id) {
        Genre genre = entityValidator.validateGenreExists(id);
        return GenreMapper.mapToGenreDto(genre);
    }
}
