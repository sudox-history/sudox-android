package ru.sudox.api.auth.entries.createusersession

/**
 * Data Transfer Object для запроса метода auth.createUserSession
 *
 * @param userSecret Токен сессии пользователя
 */
data class AuthCreateUserSessionRequestDTO(
        val userSecret: String
)