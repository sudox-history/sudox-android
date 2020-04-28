package ru.sudox.api.auth.entries.verify

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для запроса метода auth.verify
 *
 * @param authId ID сессии авторизации
 * @param publicKey Публичный ключ
 */
@SudoxApiDTO
data class AuthVerifyRequestDTO(
        val authId: String,
        val publicKey: String
)