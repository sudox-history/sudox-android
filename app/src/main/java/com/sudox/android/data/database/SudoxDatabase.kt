package com.sudox.android.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User

@Database(entities = [User::class, ChatMessage::class], version = 2, exportSchema = false)
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao(): UserDao
    abstract fun messagesDao(): ChatMessagesDao
}