package ru.yandex.practicum.filmorate.logging;

public class LogMessages {
    // Trace
    public static final String FILM_ADD = "Начало метода addFilm, на входе фильм: {}"; // film
    public static final String FILM_UPDATE = "Начало метода updateFilm, на входе фильм: {}"; // film
    public static final String FILM_DELETE = "Начало метода deleteFilm, на входе фильм с id: {}"; // id
    public static final String USER_ADD = "Начало метода registerUser, на входе пользователь: {}"; // user
    public static final String USER_UPDATE = "Начало метода updateUser, на входе пользователь: {}"; // user
    public static final String USER_DELETE = "Начало метода deleteUser, на входе пользователь с id: {}"; // id

    // Info
    public static final String FILM_SAVE_SUCCESS = "Фильм успешно сохранен и возвращен: {}"; // film
    public static final String FILM_UPDATE_SUCCESS = "Фильм успешно обновлен и возвращен: {}"; // film
    public static final String FILM_DELETE_SUCCESS = "Фильм успешно удален и возвращен: {}"; // film
    public static final String USER_SAVE_SUCCESS = "Пользователь успешно сохранен и возвращен: {}"; // user
    public static final String USER_UPDATE_SUCCESS = "Пользователь успешно обновлен и возвращен: {}"; // user
    public static final String USER_DELETE_SUCCESS = "Пользователь успешно удален и возвращен: {}"; // user

    // Debug
    public static final String FILM_SAVE_STARTED = "Начинается сохранение фильма: {}"; // film
    public static final String FILM_UPDATE_STARTED = "Начинается обновление фильма с id: {}"; // id
    public static final String FILM_DELETE_STARTED = "Начинается удаление фильма с id: {}"; // id
    public static final String USER_SAVE_STARTED = "Начинается сохранение пользователя: {}"; // user
    public static final String USER_UPDATE_STARTED = "Начинается обновление пользователя с id: {}"; // id
    public static final String USER_DELETE_STARTED = "Начинается удаление пользователя с id: {}"; // id

    // Warn
    public static final String FILM_UPDATE_NOT_FOUND = "Попытка обновить данные фильма, фильм не найден, id: {}"; // id
    public static final String FILM_DELETE_NOT_FOUND = "Попытка удалить фильм, фильм не найден, id: {}"; // id
    public static final String USER_UPDATE_NOT_FOUND = "Попытка обновить данные пользователя, пользователь не найден, id: {}"; // id
    public static final String USER_DELETE_NOT_FOUND = "Попытка удалить Пользователя, пользователь не найден, id: {}"; // id
}
