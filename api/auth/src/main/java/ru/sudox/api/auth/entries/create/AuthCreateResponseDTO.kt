package ru.sudox.api.auth.entries.create

/**
 * Data Transfer Object ответа для метода auth.create
 *
 * @param authId ID сессии авторизации
 * @param userExists Зарегистрирован ли пользователь?
 */
data class AuthCreateResponseDTO(
        val authId: String,
        val userExists: Boolean
)