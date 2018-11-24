package com.sudox.android.data.database.dao.messages

import android.arch.persistence.room.*
import com.sudox.android.data.database.model.messages.ChatMessage

@Dao
interface ChatMessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(chatMessage: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(chatMessages: List<ChatMessage>)

    @Query("DELETE FROM chat_messages WHERE peer = :recipientId OR sender = :recipientId")
    fun removeAll(recipientId: String)

    @Query("SELECT * FROM chat_messages WHERE peer = :recipientId OR sender = :recipientId ORDER BY date DESC LIMIT :offset, :limit")
    fun loadAll(recipientId: String, offset: Int, limit: Int): List<ChatMessage>
}