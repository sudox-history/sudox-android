package ru.sudox.api.auth.entries.create

import ru.sudox.api.common.SudoxApiDTO

/**
 * Тело запроса метода auth.create
 *
 * @param userPhone Номер телефона пользователя.
 */
@SudoxApiDTO
data class AuthCreateRequestBody(
        val userPhone: String
)