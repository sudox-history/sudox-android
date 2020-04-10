package ru.sudox.android.database.entities.auth

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

/**
 * Сущность сессии авторизации
 *
 * @param id ID записи в БД
 * @param phoneNumber Телефон с которым связана сессия
 * @param userExists Существует ли пользователь с таким телефоном?
 * @param token Токен сессии авторизации
 */
@Entity
data class AuthSessionEntity(
        @Id var id: Long,
        @Index var phoneNumber: String,
        var userExists: Boolean,
        var token: String
)