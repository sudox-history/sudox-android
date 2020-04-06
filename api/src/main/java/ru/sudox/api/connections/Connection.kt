package ru.sudox.api.connections

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
     * @param text Текст, который нужно отправить на сервер.
     */
    abstract fun send(text: String)

    /**
     * Прерывает соединение с сервером.
     */
    abstract fun end()
}