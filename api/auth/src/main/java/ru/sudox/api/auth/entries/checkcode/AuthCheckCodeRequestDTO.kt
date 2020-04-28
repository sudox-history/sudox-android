package ru.sudox.api.auth.entries.checkcode

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для метода auth.checkCode
 *
 * @param authId ID сессии авторизации
 * @param authCode Код подтверждения
 */
@SudoxApiDTO
data class AuthCheckCodeRequestDTO(
        val authId: String,
        val authCode: Int
)