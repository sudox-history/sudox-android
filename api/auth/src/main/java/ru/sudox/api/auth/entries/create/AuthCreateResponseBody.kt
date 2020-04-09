package ru.sudox.api.auth.entries.create

import ru.sudox.api.common.SudoxApiDTO

/**
 * Тело ответа метода auth.create
 *
 * @param authToken Токен авторизации
 * @param userExists Зарегистрирован ли пользователь?
 */
@SudoxApiDTO
data class AuthCreateResponseBody(
        val authToken: String,
        val userExists: Boolean
)