package ru.sudox.api.auth.entries.newauthverify

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для уведомления updates.newAuthVerify
 *
 * @param authId ID сессии авторизации
 * @param publicKey Публичный ключ
 */
@SudoxApiDTO
data class NewAuthVerifyDTO(
        val authId: String,
        val publicKey: String
)