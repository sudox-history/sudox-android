package ru.sudox.android

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sudox.android.auth.daos.AuthSessionDAO
import ru.sudox.android.auth.entities.AuthSessionEntity

@Database(entities = [
    AuthSessionEntity::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authSessionDao(): AuthSessionDAO
}