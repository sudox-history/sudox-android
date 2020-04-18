package ru.sudox.api.auth.entries.code

import ru.sudox.api.common.SudoxApiDTO

/**
 * Тело запроса метода auth.checkCode
 *
 * @param authCode код полученный при авторизации
 */
@SudoxApiDTO
data class AuthCheckCodeRequestBody(
        val authCode: Int
)