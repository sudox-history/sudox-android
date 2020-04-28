package ru.sudox.api.auth.entries.verify

/**
 * Data Transfer Object для запроса метода auth.verify
 *
 * @param authId ID сессии авторизации
 * @param publicKey Публичный ключ
 */
data class AuthVerifyRequestDTO(
        val authId: String,
        val publicKey: String
)