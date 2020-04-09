package ru.sudox.api.auth.entries.restore

import ru.sudox.api.common.SudoxApiDTO

/**
 * Тело запроса метода auth.restoreSession
 *
 * @param authToken Токен сессии авторизации
 */
@SudoxApiDTO
data class AuthRestoreRequestBody(
     val authToken: String
)