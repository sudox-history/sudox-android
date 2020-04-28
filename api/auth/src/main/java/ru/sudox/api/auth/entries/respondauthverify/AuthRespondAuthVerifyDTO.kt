package ru.sudox.api.auth.entries.respondauthverify

/**
 * Data Transfer Object для уведомления updates.respondAuthVerify
 *
 * @param accept Принята ли авторизация?
 * @param publicKey Публичный ключ (только если авторизация принята)
 * @param userKeyEnc Зашифрованный ключ пользователя
 */
data class AuthRespondAuthVerifyDTO(
        val accept: Boolean,
        val publicKey: String?,
        val userKeyEnc: String?
)