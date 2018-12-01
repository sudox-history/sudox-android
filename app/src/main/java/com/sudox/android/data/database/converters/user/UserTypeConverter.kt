package com.sudox.android.data.database.converters.user

import android.arch.persistence.room.TypeConverter
import com.sudox.android.data.models.users.UserType

class UserTypeConverter {

    @TypeConverter
    fun toStoringFormat(userType: UserType) = userType.name

    @TypeConverter
    fun toModelFormat(name: String) = UserType.valueOf(name)
}