package com.sudox.android.data.models.messages.chats

import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.database.model.messages.ChatMessage

data class Dialog(val user: User,
                  val message: ChatMessage)