package com.sudox.android.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sudox.android.data.database.dao.ContactsDao
import com.sudox.android.data.database.dao.UserChatMessagesDao
import com.sudox.android.data.database.model.User
import com.sudox.android.data.database.model.UserChatMessage

@Database(entities = [User::class, UserChatMessage::class], version = 1, exportSchema = false)
abstract class SudoxDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
    abstract fun messagesDao(): UserChatMessagesDao
}