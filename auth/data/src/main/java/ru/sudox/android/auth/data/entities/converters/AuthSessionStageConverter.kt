package ru.sudox.android.auth.data.entities.converters

import androidx.room.TypeConverter
import ru.sudox.android.auth.data.entities.AuthSessionStage

/**
 * Конвертер для записи в БД стадии авторизации.
 * Переводит стадию в строку с названием стадии.
 */
class AuthSessionStageConverter {

    @TypeConverter
    fun toStage(string: String): AuthSessionStage {
        return AuthSessionStage.valueOf(string)
    }

    @TypeConverter
    fun fromStage(stage: AuthSessionStage): String {
        return stage.name
    }
}