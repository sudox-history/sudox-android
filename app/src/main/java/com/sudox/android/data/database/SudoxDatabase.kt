package com.sudox.android.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.sudox.android.data.database.converters.MessageDirectionConverter
import com.sudox.android.data.database.converters.UserTypeConverter
import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.database.model.User

@Database(entities = [User::class, ChatMessage::class], version = 2, exportSchema = false)
@TypeConverters((MessageDirectionConverter::class), (UserTypeConverter::class))
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao(): UserDao
    abstract fun messagesDao(): ChatMessagesDao
}