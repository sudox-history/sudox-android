package ru.sudox.android.auth.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity сессии авторизации.
 *
 * @param phoneNumber Номер телефона
 * @param userExists Зарегистрирован ли пользователь?
 * @param creationTime Время начала сессии авторизации
 * @param stage Стадия сессии авторизации
 * @param isSelected Активна ли сессия?
 * @param authId ID сессии авторизации
 */
@Entity
data class AuthSessionEntity(
        @PrimaryKey val phoneNumber: String,
        @ColumnInfo val userExists: Boolean,
        @ColumnInfo val creationTime: Long,
        @ColumnInfo var stage: AuthSessionStage,
        @ColumnInfo var isSelected: Boolean,
        @ColumnInfo val authId: String
)