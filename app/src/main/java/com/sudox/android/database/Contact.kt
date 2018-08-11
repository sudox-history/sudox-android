package com.sudox.android.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "contacts_table")
data class Contact(@PrimaryKey var cid: Long,
              @ColumnInfo(name = "avatarJson") var avatarJson: JSONArray?,
              @ColumnInfo(name = "avatarUrl") var avatarUrl: String?,
              @ColumnInfo(name = "name") var name: String,
              @ColumnInfo(name = "nickname") var nickname: String)

