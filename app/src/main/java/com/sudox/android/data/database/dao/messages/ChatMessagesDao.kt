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
    fun removeAll(recipientId: Long)

    @Query("DELETE FROM chat_messages")
    fun removeAll()

    @Query("DELETE FROM chat_messages WHERE mid IN (:ids)")
    fun removeByIds(ids: List<Long>)

    @Query("SELECT * FROM chat_messages WHERE peer = :recipientId OR sender = :recipientId ORDER BY lid DESC LIMIT :offset, :limit")
    fun loadAll(recipientId: Long, offset: Int, limit: Int): List<ChatMessage>

    @Query("SELECT * FROM chat_messages c WHERE lid=(SELECT max(lid) FROM chat_messages WHERE sender=c.sender AND peer=c.peer OR sender=c.peer AND peer=c.sender ORDER BY lid DESC) ORDER BY lid DESC LIMIT :offset, :limit")
    fun loadAll(offset: Int, limit: Int): List<ChatMessage>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOne(message: ChatMessage)

    @Query("SELECT * FROM (SELECT * FROM chat_messages WHERE (peer = :recipientId OR sender = :recipientId) AND (status = 'DELIVERED' OR status = 'READ') ORDER by mid DESC LIMIT :offset, :limit) ORDER by mid")
    fun loadDeliveredMessages(recipientId: Long, offset: Int, limit: Int): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE status != 'DELIVERED' AND status != 'READ' ORDER BY lid")
    fun loadDeliveringMessages(): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE peer = :recipientId AND status != 'DELIVERED' AND status != 'READ' ORDER BY lid")
    fun loadDeliveringMessages(recipientId: Long): List<ChatMessage>

    @Query("SELECT * FROM chat_messages WHERE mid IN (:messagesIds)")
    fun loadByIds(messagesIds: List<Long>): List<ChatMessage>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE peer = :recipientId AND status != 'DELIVERED' AND status != 'READ'")
    fun countDeliveringMessages(recipientId: Long): Int

    @Transaction
    fun updateOrInsertMessages(messages: List<ChatMessage>) {
        val messagesIds = messages.map { it.mid }
        val storedMessages = loadByIds(messagesIds)

        // Update stored messages
        for (i in 0 until storedMessages.size) {
            val storedMessage = storedMessages[i]
            val indexOfNew = messagesIds.indexOf(storedMessage.mid)

            // Update if contains
            if (indexOfNew != -1) {
                messages[indexOfNew].apply {
                    lid = storedMessage.lid
                }
            }
        }

        insertAll(messages)
    }

    @Transaction
    fun loadMessages(recipientId: Long, offset: Int, limit: Int): List<ChatMessage> {
        val deliveredMessages = loadDeliveredMessages(recipientId, offset, limit)

        // If paging
        if (offset > 0) return deliveredMessages

        // If initial
        return buildInitialCopy(recipientId, deliveredMessages)
    }

    @Transaction
    fun buildInitialCopy(recipientId: Long, deliveredMessages: List<ChatMessage>): List<ChatMessage> {
        val deliveringMessages = loadDeliveringMessages(recipientId)
        val result = ArrayList<ChatMessage>()

        // Add to result
        result.plusAssign(deliveredMessages)
        result.plusAssign(deliveringMessages)

        return result.sortedBy { it.lid }
    }
}