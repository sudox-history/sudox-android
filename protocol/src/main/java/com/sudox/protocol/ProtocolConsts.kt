package com.sudox.protocol

internal val BASE64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$".toRegex()
internal val PACKET_MATCH_REGEX = "\\[.*?]|\\[.*|.*?]".toRegex()
internal const val BUFFER_SIZE = 8192