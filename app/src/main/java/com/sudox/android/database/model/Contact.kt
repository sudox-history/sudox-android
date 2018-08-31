package com.sudox.android.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts_table")
data class Contact(@PrimaryKey var cid: String,
                   @ColumnInfo(name = "firstColor") var firstColor: String? = null,
                   @ColumnInfo(name = "secondColor") var secondColor: String? = null,
                   @ColumnInfo(name = "avatarUrl") var avatarUrl: String? = null,
                   @ColumnInfo(name = "name") var name: String,
                   @ColumnInfo(name = "nickname") var nickname: String)

