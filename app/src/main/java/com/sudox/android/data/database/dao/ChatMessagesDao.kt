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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(chatMessages: List<ChatMessage>)

    @Transaction
    @Query("DELETE FROM chat_messages where mid NOT IN (SELECT mid from chat_messages WHERE peer = :peerId OR sender = :peerId ORDER BY date DESC LIMIT :count) AND (peer = :peerId OR sender = :peerId)")
    fun removeOldMessages(peerId: String, count: Int)

    @Transaction
    @Query("DELETE FROM chat_messages WHERE peer = :peerId OR sender = :peerId")
    fun removeMessages(peerId: String)

    @Query("SELECT * FROM chat_messages WHERE peer = :peerId OR sender = :peerId ORDER BY date DESC")
    fun loadAll(peerId: String): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE peer = :peerId OR sender = :peerId ORDER BY date DESC LIMIT :offset, :limit")
    fun loadAll(peerId: String, offset: Int, limit: Int): List<ChatMessage>
}