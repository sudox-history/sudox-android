package com.sudox.android.common.models

import com.sudox.android.database.model.Message


data class NewMessageData(val message: Message, val fromId: String)