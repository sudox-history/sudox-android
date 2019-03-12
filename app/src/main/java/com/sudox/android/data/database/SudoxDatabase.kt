package com.sudox.android.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.sudox.android.data.database.converters.messages.MessageDirectionConverter
import com.sudox.android.data.database.converters.messages.MessageStatusConverter
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.dao.messages.DialogMessagesDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.database.model.messages.DialogMessage

@Database(entities = [User::class, DialogMessage::class], version = 3, exportSchema = false)
@TypeConverters((MessageDirectionConverter::class), (MessageStatusConverter::class))
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messagesDao(): DialogMessagesDao
}