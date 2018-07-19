package com.sudox.protocol.model.dto

data class HandshakeUpgradeToServerDTO(val payload: String, val hash: String)
data class HandshakeUpgradeFromServerDTO(val result: Int)