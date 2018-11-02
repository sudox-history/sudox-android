package com.sudox.android.data.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sudox.android.data.database.model.ChatMessage

@Dao
interface ChatMessagesDao {

    @Query("SELECT * FROM chat_messages WHERE peer = :peerId AND sender = :peerId ORDER by date")
    fun loadMessagesByPeer(peerId: String): LiveData<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(chatMessage: ChatMessage)
}