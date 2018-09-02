package com.sudox.android.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "messages_table")
class Message(@PrimaryKey var mid: String,
              @ColumnInfo var text: String,
              @ColumnInfo var time: Long,
              @ColumnInfo var type: Int,
              @ColumnInfo var userId: String)