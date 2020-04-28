package ru.sudox.api.auth.entries.signin

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для ответа метода auth.signIn
 *
 * @param userId ID пользователя
 * @param userSecret Токен сессии пользователя
 */
@SudoxApiDTO
data class AuthSignInResponseDTO(
        val userId: String,
        val userSecret: String
)