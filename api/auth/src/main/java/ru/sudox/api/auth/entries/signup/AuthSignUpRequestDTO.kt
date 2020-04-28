package ru.sudox.api.auth.entries.signup

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для запроса метода auth.signUp
 *
 * @param authId ID сессии авторизации
 * @param userName Имя пользователя
 * @param userNickname Никнейм пользователя
 * @param userKeyHash Хеш ключа пользователя (по BLAKE2b)
 */
@SudoxApiDTO
data class AuthSignUpRequestDTO(
        val authId: String,
        val userName: String,
        val userNickname: String,
        val userKeyHash: String
)