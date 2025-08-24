package ru.yandex.practicum.filmorate.logging;

public class LogMessages {
    // Trace
    public static final String FILM_ADD = "Начало метода addFilm, film: {}"; // film
    public static final String FILM_UPDATE = "Начало метода updateFilm, film: {}"; // film
    public static final String FILM_DELETE = "Начало метода deleteFilm, id: {}"; // id
    public static final String USER_ADD = "Начало метода registerUser, user: {}"; // user
    public static final String USER_UPDATE = "Начало метода updateUser, user: {}"; // user
    public static final String USER_DELETE = "Начало метода deleteUser, id: {}"; // id

    // Info
    public static final String FILM_SAVE_SUCCESS = "Фильм успешно сохранен, film: {}"; // film
    public static final String FILM_UPDATE_SUCCESS = "Фильм успешно обновлен, film: {}"; // film
    public static final String FILM_DELETE_SUCCESS = "Фильм успешно удален, film: {}"; // film
    public static final String USER_SAVE_SUCCESS = "Пользователь успешно сохранен, user: {}"; // user
    public static final String USER_UPDATE_SUCCESS = "Пользователь успешно обновлен, user: {}"; // user
    public static final String USER_DELETE_SUCCESS = "Пользователь успешно удален, user: {}"; // user

    // Debug
    public static final String FILM_SAVE_STARTED = "Сохранение фильма: {}"; // film
    public static final String FILM_UPDATE_STARTED = "Обновление фильма с id: {}"; // id
    public static final String FILM_DELETE_STARTED = "Удаление фильма с id: {}"; // id
    public static final String USER_SAVE_STARTED = "Сохранение пользователя: {}"; // user
    public static final String USER_UPDATE_STARTED = "Обновление пользователя с id: {}"; // id
    public static final String USER_DELETE_STARTED = "Удаление пользователя с id: {}"; // id

    // Warn
    public static final String FILM_UPDATE_NOT_FOUND = "Попытка обновить фильм, фильм не найден, id: {}"; // id
    public static final String FILM_DELETE_NOT_FOUND = "Попытка удалить фильм, фильм не найден, id: {}"; // id
    public static final String USER_UPDATE_NOT_FOUND = "Попытка обновить пользователя, пользователь не найден, id: {}"; // id
    public static final String USER_DELETE_NOT_FOUND = "Попытка удалить пользователя, пользователь не найден, id: {}"; // id
}
