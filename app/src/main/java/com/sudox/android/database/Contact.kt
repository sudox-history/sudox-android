package com.sudox.android.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(@PrimaryKey var id: Long,
              @ColumnInfo(name = "avatarJson") var avatarJson: String?,
              @ColumnInfo(name = "avatarUrl") var avatarUrl: String?,
              @ColumnInfo(name = "name") var name: String,
              @ColumnInfo(name = "nickname") var nickname: String)

