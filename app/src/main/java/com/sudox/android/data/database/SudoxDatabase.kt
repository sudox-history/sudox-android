package com.sudox.android.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.sudox.android.data.database.converters.messages.MessageDirectionConverter
import com.sudox.android.data.database.converters.messages.MessageStatusConverter
import com.sudox.android.data.database.converters.user.UserTypeConverter
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.database.model.messages.ChatMessage

@Database(entities = [User::class, ChatMessage::class], version = 3, exportSchema = false)
@TypeConverters((MessageDirectionConverter::class), (MessageStatusConverter::class), (UserTypeConverter::class))
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messagesDao(): ChatMessagesDao
}