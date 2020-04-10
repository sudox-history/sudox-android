package ru.sudox.android.database.entities.auth

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import ru.sudox.android.database.encryption.converters.EncryptedStringConverter

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
        @Id var id: Long = 0L,
        @Index var phoneNumber: String,
        var userExists: Boolean,
        @Convert(converter = EncryptedStringConverter::class, dbType = ByteArray::class) var token: String
)