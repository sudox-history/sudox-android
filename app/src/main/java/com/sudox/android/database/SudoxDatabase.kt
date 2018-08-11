package com.sudox.android.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao()
}