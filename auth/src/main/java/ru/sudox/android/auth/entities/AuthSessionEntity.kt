package ru.sudox.android.auth.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AuthSessionEntity(
        @PrimaryKey val phoneNumber: String,
        @ColumnInfo val userExists: Boolean,
        @ColumnInfo val token: String
)