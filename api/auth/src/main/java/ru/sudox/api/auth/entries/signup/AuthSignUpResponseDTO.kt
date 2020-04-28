package ru.sudox.api.auth.entries.signup

/**
 * Data Transfer Object для ответа метода auth.signUp
 *
 * @param userId ID зарегистрированного пользователя
 * @param userSecret Токен сессии пользователя.
 */
data class AuthSignUpResponseDTO(
        val userId: String,
        val userSecret: String
)