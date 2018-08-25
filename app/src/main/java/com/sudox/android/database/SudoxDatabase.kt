package com.sudox.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sudox.android.database.model.Contact
import com.sudox.android.database.dao.ContactsDao

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
}