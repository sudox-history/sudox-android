package ru.sudox.android

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sudox.android.auth.data.daos.AuthSessionDAO
import ru.sudox.android.auth.data.entities.AuthSessionEntity

@Database(entities = [
    ru.sudox.android.auth.data.entities.AuthSessionEntity::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authSessionDao(): ru.sudox.android.auth.data.daos.AuthSessionDAO
}