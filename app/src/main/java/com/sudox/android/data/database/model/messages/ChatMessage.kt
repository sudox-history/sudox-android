package com.sudox.android.data.database.model.messages

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.sudox.android.data.models.messages.MessageDirection

@Entity(tableName = "chat_messages")
data class ChatMessage(@PrimaryKey var mid: String,
                       @ColumnInfo(index = true) var sender: String,
                       @ColumnInfo(index = true) var peer: String,
                       @ColumnInfo var message: String,
                       @ColumnInfo var date: Long,
                       @ColumnInfo var type: MessageDirection)