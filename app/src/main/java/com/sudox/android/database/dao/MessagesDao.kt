package com.sudox.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sudox.android.database.model.Message

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table")
    fun getMessages(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)
}