package com.sudox.protocol.model

import com.sudox.protocol.model.dto.JsonModel

interface MessageCallback {
    fun onMessage(jsonModel: JsonModel)
}