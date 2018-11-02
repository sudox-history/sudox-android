package com.sudox.android.data.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sudox.android.data.database.model.ChatMessage

@Dao
interface ChatMessagesDao {

    @Query("SELECT * FROM chat_messages WHERE peer = :peerId OR sender = :peerId ORDER by date")
    fun loadMessagesByPeer(peerId: String): LiveData<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(chatMessage: ChatMessage)

    @Transaction
    @Query("DELETE FROM chat_messages where mid NOT IN (SELECT mid from chat_messages ORDER BY mid DESC LIMIT 100)")
    fun removeOldMessages()
}