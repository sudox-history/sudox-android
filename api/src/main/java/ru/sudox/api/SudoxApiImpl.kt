package ru.sudox.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.connections.Connection
import ru.sudox.api.connections.ConnectionListener
import ru.sudox.api.entries.ApiRequest
import ru.sudox.api.entries.ApiRequestCallback
import ru.sudox.api.exceptions.ApiException
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.sudox.api.common.SudoxApiStatus
import java.io.IOException
import java.util.concurrent.Semaphore

/**
 * Основной обьект API.
 * Осуществляет взаимодействие с соединением и сериализацией.
 * Отправляет пакеты на сервер, а также принимает их и доставляет получателю.
 *
 * @param connection Обьект соединения.
 * @param objectMapper Маппер обьектов.
 */
class SudoxApiImpl(
        val connection: Connection,
        val objectMapper: ObjectMapper
) : ConnectionListener, SudoxApi {

    private val endSemaphore = Semaphore(0)

    val requestsSemaphores = LinkedHashMap<String, Semaphore>()
    val requestsCallbacks = LinkedHashMap<String, ApiRequestCallback<*>>()

    override val statusSubject: PublishSubject<SudoxApiStatus> = PublishSubject.create()
    override var isConnected = false
        set(value) {
            field = value

            if (!value) {
                // Разрываем запросы, на которые ожидаем ответа
                requestsCallbacks.forEach {
                    it.value.subjectEmitter.onError(IOException("Connection not installed!"))
                    it.value.subjectEmitter.onComplete()
                }

                // Разрываем запросы, которые ожидают выполнения себе подобных
                requestsSemaphores.forEach { (_, semaphore) ->
                    semaphore.release(semaphore.queueLength)
                }

                requestsSemaphores.clear()
            }

            statusSubject.onNext(if (value) {
                SudoxApiStatus.CONNECTED
            } else {
                SudoxApiStatus.NOT_CONNECTED
            })
        }

    init {
        connection.listener = this
    }

    override fun startConnection() {
        if (isConnected) {
            endConnection()
        }

        connection.start("sudox.ru", 5000)
    }

    override fun endConnection() {
        connection.end()
        endSemaphore.acquire()
    }

    override fun <T : Any> sendRequest(
            methodName: String,
            requestData: Any,
            responseClass: Class<T>
    ): Observable<T> = Observable.create<T> {
        if (isConnected) {
            acquireRequestQueue(methodName)

            // P.S.: Соединение могло быть разорвано за время ожидания!
            if (isConnected) {
                requestsCallbacks[methodName] = ApiRequestCallback<T>(it, responseClass)

                val request = ApiRequest(methodName, requestData)
                val serialized = objectMapper.writeValueAsString(request)

                connection.send(serialized)
            } else {
                releaseRequestQueue(methodName)
                it.onError(IOException("Connection not installed!"))
                it.onComplete()
            }
        } else {
            releaseRequestQueue(methodName)
            it.onError(IOException("Connection not installed!"))
            it.onComplete()
        }
    }

    /**
     * Разблокировывает очередь запросов с определенным методом.
     *
     * @param methodName Название метода.
     */
    fun releaseRequestQueue(methodName: String) {
        requestsSemaphores[methodName]?.let {
            it.release()

            if (it.queueLength == 0) {
                requestsSemaphores.remove(methodName)
                requestsCallbacks.remove(methodName)
            }
        }
    }

    /**
     * Блокирует очередь запросов с определенным методом.
     *
     * @param methodName Название метода.
     */
    fun acquireRequestQueue(methodName: String) {
        requestsSemaphores
                .getOrPut(methodName, { Semaphore(1) })
                .acquire()
    }

    override fun onStart() {
        isConnected = true
    }

    override fun onEnd() {
        isConnected = false
        endSemaphore.release()
    }

    override fun onReceive(text: String) {
        var methodName: String? = null
        var emitter: ObservableEmitter<*>? = null

        try {
            val response = objectMapper.readTree(text)
            val methodNameNode = response.required("method_name")
            val resultNode = response.required("result")

            if (!methodNameNode.isTextual || !resultNode.isInt) {
                return
            }

            methodName = methodNameNode.textValue()

            val callback = requestsCallbacks[methodName] ?: return
            val result = resultNode.intValue()

            // TODO: Обработка уведомлений

            @Suppress("UNCHECKED_CAST")
            emitter = callback.subjectEmitter as ObservableEmitter<Any>

            if (result == OK_ERROR_CODE) {
                val dataNode = response.required("data")

                if (!dataNode.isObject) {
                    releaseRequestQueue(methodName)
                    return
                }

                emitter.onNext(objectMapper.treeToValue(dataNode, callback.dataClass))
            } else {
                emitter.onError(ApiException(result))
            }

            emitter.onComplete()

            releaseRequestQueue(methodName)
        } catch (ex: Exception) {
            if (methodName != null) {
                releaseRequestQueue(methodName)
            }

            emitter?.onError(ex)
            emitter?.onComplete()
        }
    }

    override fun onReceive(bytes: ByteArray) {
        // Nothing
    }
}