package com.sudox.android.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudox.android.database.model.Message

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE userId =:mid")
    fun getMessages(mid: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(messages: List<Message>)
}