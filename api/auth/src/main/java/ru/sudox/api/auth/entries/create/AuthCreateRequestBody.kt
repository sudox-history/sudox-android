package ru.sudox.api.auth.entries.create

/**
 * Тело запроса метода auth.create
 *
 * @param userPhone Номер телефона пользователя.
 */
data class AuthCreateRequestBody(
        val userPhone: String
)