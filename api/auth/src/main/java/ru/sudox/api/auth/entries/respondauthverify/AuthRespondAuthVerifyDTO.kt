package ru.sudox.api.auth.entries.respondauthverify

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для уведомления updates.respondAuthVerify
 *
 * @param accept Принята ли авторизация?
 * @param publicKey Публичный ключ (только если авторизация принята)
 * @param userKeyEnc Зашифрованный ключ пользователя
 */
@SudoxApiDTO
data class AuthRespondAuthVerifyDTO(
        val accept: Boolean,
        val publicKey: String?,
        val userKeyEnc: String?
)