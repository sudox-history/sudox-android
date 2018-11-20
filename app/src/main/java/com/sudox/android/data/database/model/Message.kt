package com.sudox.android.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(@PrimaryKey var mid: String,
                       @ColumnInfo var sender: String,
                       @ColumnInfo var peer: String,
                       @ColumnInfo var message: String,
                       @ColumnInfo var date: Long,
                       @ColumnInfo var type: Int)