package ru.sudox.api.auth.entries.create

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object запроса для метода auth.create
 *
 * @param userPhone Номер телефона пользователя (без +)
 */
@SudoxApiDTO
data class AuthCreateRequestDTO(val userPhone: String)