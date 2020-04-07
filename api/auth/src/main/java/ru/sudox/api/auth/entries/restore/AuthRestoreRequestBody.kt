package ru.sudox.api.auth.entries.restore

/**
 * Тело запроса метода auth.restoreSession
 *
 * @param authToken Токен сессии авторизации
 */
data class AuthRestoreRequestBody(
     val authToken: String
)