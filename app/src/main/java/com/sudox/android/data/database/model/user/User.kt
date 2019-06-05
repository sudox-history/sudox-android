package com.sudox.android.data.database.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(@PrimaryKey var uid: Long,
                @ColumnInfo var name: String,
                @ColumnInfo var nickname: String,
                @ColumnInfo var photo: String,
                @ColumnInfo var phone: String? = null,
                @ColumnInfo var status: String? = null,
                @ColumnInfo var bio: String? = null,
                @ColumnInfo var isContact: Boolean = false) : Serializable {

    fun separateNickname() = nickname.split("#")
}