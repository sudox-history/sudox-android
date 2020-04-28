package ru.sudox.api.auth.entries.signin

/**
 * Data Transfer Object для запроса метода auth.signIn
 *
 * @param authId ID сессии авторизации
 * @param userKeyHash Хеш ключа пользователя
 */
data class AuthSignInRequestDTO(
        val authId: String,
        val userKeyHash: String
)