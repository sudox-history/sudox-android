package com.sudox.api.connections

/**
 * Слушатель соединения.
 * Доставляет события на соединении к получателю
 */
interface ConnectionListener {

    /**
     * Вызывается при успешной установке соединения.
     */
    fun onStart()

    /**
     * Вызывается при получении сообщения в байтах.
     *
     * @param bytes Полученные байты.
     */
    fun onReceive(bytes: ByteArray)

    /**
     * Вызывается при получении сообщения в строке.
     *
     * @param text Полученная строка.
     */
    fun onReceive(text: String)

    /**
     * Вызывается при ошибке соединения с сервером.
     */
    fun onEnd()
}