package com.sudox.android.data.database.model.user

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.sudox.android.data.models.users.UserType
import java.io.Serializable

@Entity(tableName = "users")
data class User(@PrimaryKey var uid: String,
                @ColumnInfo var name: String,
                @ColumnInfo var nickname: String,
                @ColumnInfo var photo: String,
                @ColumnInfo var phone: String? = null,
                @ColumnInfo var status: String? = null,
                @ColumnInfo var bio: String? = null,
                @ColumnInfo(index = true) var type: UserType) : Serializable