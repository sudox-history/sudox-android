package ru.sudox.api.auth.entries.respondverify

/**
 * Data Transfer Object для запроса метода auth.respondVerify
 *
 * @param accept Одобрить ли запрос?
 * @param publicKey Публичный ключ
 * @param authId ID сессии авторизации
 * @param userKeyEnc Зашифрованный ключ пользователя
 */
data class AuthRespondVerifyRequestDTO(
        val accept: Boolean,
        val publicKey: String,
        val authId: String,
        val userKeyEnc: String
)