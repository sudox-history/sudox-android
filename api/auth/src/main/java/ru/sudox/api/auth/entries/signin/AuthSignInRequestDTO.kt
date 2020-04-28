package ru.sudox.api.auth.entries.signin

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для запроса метода auth.signIn
 *
 * @param authId ID сессии авторизации
 * @param userKeyHash Хеш ключа пользователя
 */
@SudoxApiDTO
data class AuthSignInRequestDTO(
        val authId: String,
        val userKeyHash: String
)