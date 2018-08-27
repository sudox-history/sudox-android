package com.sudox.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sudox.android.database.model.Message

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE userId =:mid")
    fun getMessages(mid: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)
}