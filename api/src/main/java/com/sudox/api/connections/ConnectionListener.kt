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
     * Вызывается при получении сообщения.
     *
     * @param bytes Полученные байты.
     */
    fun onReceive(bytes: ByteArray)

    /**
     * Вызывается при ошибке соединения с сервером.
     */
    fun onEnd()
}