package com.sudox.protocol.models.enums

enum class ConnectionState {
    CONNECT_ERRORED, // Только при вызове метода connect()
    CONNECT_SUCCEED, // Только при вызове метода connect()
    HANDSHAKE_SUCCEED, // Хэндшейк пройден (использовать при установлении сессии)
    CONNECTION_CLOSED, // Соединение закрылось
}