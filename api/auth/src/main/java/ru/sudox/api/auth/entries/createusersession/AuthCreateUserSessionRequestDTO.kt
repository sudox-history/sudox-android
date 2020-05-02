package ru.sudox.api.auth.entries.createusersession

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для запроса метода auth.createUserSession
 *
 * @param userId ID пользователя
 * @param userSecret Токен сессии пользователя
 */
@SudoxApiDTO
data class AuthCreateUserSessionRequestDTO(
        val userId: String,
        val userSecret: String
)