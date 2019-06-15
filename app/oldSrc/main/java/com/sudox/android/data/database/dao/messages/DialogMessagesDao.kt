package com.sudox.android.data.database.dao.messages

import androidx.room.*
import com.sudox.android.data.database.model.messages.DialogMessage

@Dao
interface DialogMessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(dialogMessage: DialogMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(dialogMessages: List<DialogMessage>)

    @Query("DELETE FROM dialogs_messages WHERE peer = :recipientId OR sender = :recipientId")
    fun removeAll(recipientId: Long)

    @Query("DELETE FROM dialogs_messages WHERE (peer = :recipientId OR sender = :recipientId) AND (status = 'DELIVERED' OR status = 'READ')")
    fun removeAllDeliveredMessages(recipientId: Long)

    @Query("DELETE FROM dialogs_messages WHERE (peer IN (:recipientsIds) OR sender IN (:recipientsIds)) AND (status = 'DELIVERED' OR status = 'READ')")
    fun removeAllDeliveredMessages(recipientsIds: List<Long>)

    @Query("DELETE FROM dialogs_messages WHERE (status = 'DELIVERED' OR status = 'READ')")
    fun removeAllDeliveredMessages()

    @Query("DELETE FROM dialogs_messages")
    fun removeAll()

    @Query("DELETE FROM dialogs_messages WHERE mid IN (:ids)")
    fun removeByIds(ids: List<Long>)

    @Query("SELECT * FROM dialogs_messages WHERE peer = :recipientId OR sender = :recipientId ORDER BY mid DESC LIMIT :offset, :limit")
    fun loadAll(recipientId: Long, offset: Int, limit: Int): List<DialogMessage>

    @Query("SELECT * FROM dialogs_messages c WHERE date=(SELECT max(date) FROM dialogs_messages WHERE sender=c.sender AND peer=c.peer OR sender=c.peer AND peer=c.sender ORDER BY mid DESC) ORDER BY date DESC LIMIT :offset, :limit")
    fun loadLastMessages(offset: Int, limit: Int): List<DialogMessage>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOne(message: DialogMessage)

    @Query("SELECT * FROM (SELECT * FROM dialogs_messages WHERE (peer = :recipientId OR sender = :recipientId) AND (status = 'DELIVERED' OR status = 'READ') ORDER by mid DESC LIMIT :offset, :limit) ORDER by mid")
    fun loadDeliveredMessages(recipientId: Long, offset: Int, limit: Int): List<DialogMessage>

    @Query("SELECT * FROM dialogs_messages WHERE status != 'DELIVERED' AND status != 'READ' ORDER BY lid DESC LIMIT :offset, :limit")
    fun loadLastDeliveringMessages(offset: Int, limit: Int): List<DialogMessage>

    @Query("SELECT * FROM dialogs_messages WHERE peer = :recipientId AND status != 'DELIVERED' AND status != 'READ' ORDER BY date")
    fun loadDeliveringMessages(recipientId: Long): List<DialogMessage>

    @Query("SELECT * FROM dialogs_messages WHERE mid IN (:messagesIds)")
    fun loadByIds(messagesIds: List<Long>): List<DialogMessage>

    @Query("SELECT COUNT(*) FROM dialogs_messages WHERE peer = :recipientId AND status != 'DELIVERED' AND status != 'READ'")
    fun countDeliveringMessages(recipientId: Long): Int

    @Transaction
    fun updateOrInsertMessages(messages: List<DialogMessage>) {
        val messagesIds = messages.map { it.mid }
        val storedMessages = loadByIds(messagesIds)

        // Remove all
        removeByIds(messagesIds)

        // Update stored messages
        for (i in 0 until storedMessages.size) {
            val storedMessage = storedMessages[i]
            val indexOfNew = messagesIds.indexOf(storedMessage.mid)

            // Update if contains
            if (indexOfNew != -1) {
                messages[indexOfNew].apply {
                    mid = storedMessage.mid
                }
            }
        }

        insertAll(messages)
    }

    @Transaction
    fun loadMessages(recipientId: Long, offset: Int, limit: Int): List<DialogMessage> {
        val deliveredMessages = loadDeliveredMessages(recipientId, offset, limit)

        // If paging
        if (offset > 0) return deliveredMessages

        // If initial
        return buildInitialCopy(recipientId, deliveredMessages)
    }

    @Transaction
    fun buildInitialCopy(recipientId: Long, deliveredMessages: List<DialogMessage>): ArrayList<DialogMessage> {
        val deliveringMessages = loadDeliveringMessages(recipientId)
        val result = ArrayList<DialogMessage>()

        // Add to result
        result.plusAssign(deliveredMessages.sortedBy { it.mid })
        result.plusAssign(deliveringMessages.sortedBy { it.date })

        return result
    }
}