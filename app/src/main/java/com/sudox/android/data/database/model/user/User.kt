package com.sudox.android.data.database.model.user

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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