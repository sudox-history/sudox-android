package com.sudox.android.common.models

import com.sudox.android.common.enums.SendMessageState
import com.sudox.android.database.model.Message

data class SendMessageData(val sendMessageState: SendMessageState, val message: Message?=null)