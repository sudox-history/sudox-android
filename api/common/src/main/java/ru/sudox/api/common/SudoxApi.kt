package ru.sudox.api.common

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface SudoxApi {

    val statusSubject: PublishSubject<SudoxApiStatus>
    var isConnected: Boolean

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
    fun <T : Any> sendRequest(methodName: String, requestData: Any, responseClass: Class<T>): Observable<T>
}