package com.sudox.api.connections

/**
 * Интерфейс соединения.
 *
 * Занимается поставкой байтов на сервер и обратно.
 * Также контроллирует свой статус.
 */
abstract class Connection {

    var listener: ConnectionListener? = null

    /**
     * Устанавливает соединение с сервером
     *
     * @param address Адрес сервера
     * @param port Порт сервера
     */
    abstract fun start(address: String, port: Int)

    /**
     * Отправляет информацию на сервер.
     *
     * @param bytes Байты, которые нужно отправить на сервер.
     */
    abstract fun sendData(bytes: ByteArray)

    /**
     * Прерывает соединение с сервером.
     */
    abstract fun end()
}