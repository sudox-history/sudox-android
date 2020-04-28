package ru.sudox.api.auth.entries.signup

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для ответа метода auth.signUp
 *
 * @param userId ID зарегистрированного пользователя
 * @param userSecret Токен сессии пользователя.
 */
@SudoxApiDTO
data class AuthSignUpResponseDTO(
        val userId: String,
        val userSecret: String
)