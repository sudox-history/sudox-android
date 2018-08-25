package com.sudox.android.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages_table")
class Message(@PrimaryKey var mid: Long,
              @ColumnInfo var text: String,
              @ColumnInfo var time: String)