package com.sudox.android.data.models.messages.dialogs

import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.database.model.messages.DialogMessage

data class Dialog(val recipient: User,
                  val lastMessage: DialogMessage)