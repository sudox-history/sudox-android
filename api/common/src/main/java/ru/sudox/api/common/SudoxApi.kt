package ru.sudox.api.common

import io.reactivex.rxjava3.core.Single

interface SudoxApi {

    /**
     * Запускает соединение с сервером.
     */
    fun startConnection()

    /**
     * Останавливает соединение с сервером.
     */
    fun endConnection()

    /**
     * Отправляет запрос на сервер.
     *
     * @param methodName Название вызываемого метода.
     * @param requestData Данные для запроса.
     * @param responseClass Класс с информацией ответа
     */
    fun <T : Any> sendRequest(methodName: String, requestData: Any, responseClass: Class<T>): Single<T>
}