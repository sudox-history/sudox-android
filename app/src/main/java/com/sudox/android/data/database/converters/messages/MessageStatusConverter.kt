package com.sudox.android.data.database.converters.messages

import androidx.room.TypeConverter
import com.sudox.android.data.models.messages.MessageStatus

class MessageStatusConverter {

    @TypeConverter
    fun toStoringFormat(messageStatus: MessageStatus) = messageStatus.name

    @TypeConverter
    fun toModelFormat(name: String) = MessageStatus.valueOf(name)
}