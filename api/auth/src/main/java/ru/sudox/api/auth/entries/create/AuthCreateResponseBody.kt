package ru.sudox.api.auth.entries.create

/**
 * Тело ответа метода auth.create
 *
 * @param authToken Токен авторизации
 * @param userExists Зарегистрирован ли пользователь?
 */
data class AuthCreateResponseBody(
        val authToken: String,
        val userExists: Boolean
)