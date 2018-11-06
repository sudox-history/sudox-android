package com.sudox.android.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sudox.android.data.database.dao.ContactsDao
import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.model.User
import com.sudox.android.data.database.model.ChatMessage

@Database(entities = [User::class, ChatMessage::class], version = 2, exportSchema = false)
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
    abstract fun messagesDao(): ChatMessagesDao
}