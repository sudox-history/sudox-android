package com.sudox.android.data.database.dao.messages

import android.arch.persistence.room.*
import com.sudox.android.data.database.model.messages.ChatMessage

@Dao
interface ChatMessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(chatMessage: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(chatMessages: List<ChatMessage>)

    @Query("DELETE FROM chat_messages WHERE peer = :recipientId OR sender = :recipientId")
    fun removeAll(recipientId: String)

    @Query("DELETE FROM chat_messages")
    fun removeAll()

    @Query("SELECT * FROM chat_messages WHERE peer = :recipientId OR sender = :recipientId ORDER BY date DESC LIMIT :offset, :limit")
    fun loadAll(recipientId: String, offset: Int, limit: Int): List<ChatMessage>

    @Query("SELECT * FROM chat_messages c WHERE date=(SELECT max(date) FROM chat_messages WHERE sender=c.sender AND peer=c.peer OR sender=c.peer AND peer=c.sender ORDER BY date DESC) ORDER BY date DESC LIMIT :offset, :limit")
    fun loadAll(offset: Int, limit: Int): List<ChatMessage>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOne(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE status != 'DELIVERED' AND status != 'READ' ORDER BY lid")
    fun loadDeliveringMessages(): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE peer = :recipientId AND status != 'DELIVERED' AND status != 'READ' ORDER BY lid")
    fun loadDeliveringMessages(recipientId: String): List<ChatMessage>
}