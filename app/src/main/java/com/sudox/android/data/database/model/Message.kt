package com.sudox.android.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "user_chat_messages")
class UserChatMessage(@PrimaryKey var mid: Int,
                      @ColumnInfo var text: String,
                      @ColumnInfo var date: Long,
                      @ColumnInfo var type: Int,
                      @ColumnInfo var userId: String)