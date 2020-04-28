package ru.sudox.api.auth.entries.signin

/**
 * Data Transfer Object для ответа метода auth.signIn
 *
 * @param userId ID пользователя
 * @param userSecret Токен сессии пользователя
 */
data class AuthSignInResponseDTO(
        val userId: String,
        val userSecret: String
)