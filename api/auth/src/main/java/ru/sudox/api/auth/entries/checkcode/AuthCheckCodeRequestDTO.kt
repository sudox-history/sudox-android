package ru.sudox.api.auth.entries.checkcode

/**
 * Data Transfer Object для метода auth.checkCode
 *
 * @param authId ID сессии авторизации
 * @param authCode Код подтверждения
 */
data class AuthCheckCodeRequestDTO(
        val authId: String,
        val authCode: Int
)