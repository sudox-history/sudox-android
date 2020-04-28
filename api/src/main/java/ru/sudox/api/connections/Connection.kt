package ru.sudox.api.connections

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
     * @param bytes Байты, которые должны быть отправлены на сервер.
     */
    abstract fun send(bytes: ByteArray)

    /**
     * Закрывает соединение с сервером.
     */
    abstract fun close()
}