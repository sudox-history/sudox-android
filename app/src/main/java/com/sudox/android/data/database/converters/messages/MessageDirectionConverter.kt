package com.sudox.android.data.database.converters.messages

import androidx.room.TypeConverter
import com.sudox.android.data.models.messages.MessageDirection

class MessageDirectionConverter {

    @TypeConverter
    fun toStoringFormat(messageDirection: MessageDirection) = messageDirection.name

    @TypeConverter
    fun toModelFormat(name: String) = MessageDirection.valueOf(name)
}