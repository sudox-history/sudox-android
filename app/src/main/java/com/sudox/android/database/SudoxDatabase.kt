package com.sudox.android.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sudox.android.database.dao.ContactsDao
import com.sudox.android.database.dao.MessagesDao
import com.sudox.android.database.model.Contact
import com.sudox.android.database.model.Message

@Database(entities = [Contact::class, Message::class], version = 1, exportSchema = false)
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
    abstract fun messagesDao(): MessagesDao
}