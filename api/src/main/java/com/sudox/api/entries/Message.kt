package com.sudox.api.entries

import com.sudox.api.serializers.Serializer

/**
 * Интерфейс сообщения.
 * Занимается десериализацией и сериализацией данных внутри себя.
 */
interface Message {

    fun writeData(serializer: Serializer)
}