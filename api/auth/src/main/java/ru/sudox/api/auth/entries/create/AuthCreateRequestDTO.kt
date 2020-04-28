package ru.sudox.api.auth.entries.create

/**
 * Data Transfer Object запроса для метода auth.create
 *
 * @param userPhone Номер телефона пользователя (без +)
 */
data class AuthCreateRequestDTO(val userPhone: String)