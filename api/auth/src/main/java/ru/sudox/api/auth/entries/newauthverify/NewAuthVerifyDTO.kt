package ru.sudox.api.auth.entries.newauthverify

/**
 * Data Transfer Object для уведомления updates.newAuthVerify
 *
 * @param publicKey Публичный ключ
 */
data class NewAuthVerifyDTO(val publicKey: String)