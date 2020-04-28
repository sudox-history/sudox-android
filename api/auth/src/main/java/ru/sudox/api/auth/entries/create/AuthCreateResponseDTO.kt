package ru.sudox.api.auth.entries.create

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object ответа для метода auth.create
 *
 * @param authId ID сессии авторизации
 * @param userExists Зарегистрирован ли пользователь?
 */
@SudoxApiDTO
data class AuthCreateResponseDTO(
        val authId: String,
        val userExists: Boolean
)