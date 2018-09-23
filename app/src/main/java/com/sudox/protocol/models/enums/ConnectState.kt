package com.sudox.protocol.models.enums

enum class ConnectState {
    CONNECT_ERROR,
    DISCONNECTED,
    RECONNECTED,
    ATTACKED,
    MISSING_TOKEN,
    CORRECT_TOKEN,
    WRONG_TOKEN
}