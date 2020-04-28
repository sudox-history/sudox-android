package ru.sudox.api.auth.entries.newauthverify

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для уведомления updates.newAuthVerify
 *
 * @param publicKey Публичный ключ
 */
@SudoxApiDTO
data class NewAuthVerifyDTO(val publicKey: String)