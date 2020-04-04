package com.sudox.api.connections

/**
 * Интерфейс соединения.
 *
 * Занимается поставкой байтов на сервер и обратно.
 * Также контроллирует свой статус.
 */
interface Connection {

    /**
     * Устанавливает соединение с сервером
     *
     * @param address Адрес сервера
     * @param port Порт сервера
     */
    fun start(address: String, port: Int)

    /**
     * Устанавливает слушателя соединения.
     * Может быть только один слушатель.
     *
     * @param listener Слушатель соединения.
     */
    fun setListener(listener: ConnectionListener)

    /**
     * Отправляет информацию на сервер.
     *
     * @param bytes Байты, которые нужно отправить на сервер.
     */
    fun sendData(bytes: ByteArray)
}