package com.sudox.api.serializers

import com.sudox.api.entries.Message

/**
 * Сериализатор сообщений.
 * Подготавливает данные для отправки в Connection и наоборот из него.
 */
interface Serializer {
    
    /**
     * Сериализует сообщение в байты
     *
     * @param message Сообщение, которое нужно сериализовать
     * @return Байты, полученные в ходе сериализации
     */
    fun serialize(message: Message): ByteArray

    /**
     * Десериализует сообщения из набора байтов.
     *
     * @param bytes Байты, которые нужно десериализовать
     * @return Десериализованное сообщение
     */
    fun deserialize(bytes: ByteArray): Message
}