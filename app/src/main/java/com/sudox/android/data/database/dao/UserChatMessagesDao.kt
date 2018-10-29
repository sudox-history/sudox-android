package com.sudox.android.data.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudox.android.data.database.model.UserChatMessage

@Dao
interface UserChatMessagesDao {

    @Query("SELECT * FROM user_chat_messages WHERE userId = :recipientId ORDER by date")
    fun loadMessagesWithRecipient(recipientId: String): LiveData<List<UserChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(userChatMessage: UserChatMessage)
}