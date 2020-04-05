package com.sudox.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sudox.api.common.SudoxApi
import com.sudox.api.connections.Connection
import com.sudox.api.connections.ConnectionListener
import com.sudox.api.entries.ApiRequest
import com.sudox.api.entries.ApiRequestCallback
import com.sudox.api.exceptions.ApiException
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.SingleSubject
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

    val requestsSemaphores = LinkedHashMap<String, Semaphore>()
    val requestsCallbacks = LinkedHashMap<String, ApiRequestCallback<*>>()
    val statusSubject: PublishSubject<SudoxApiStatus> = PublishSubject.create()
    var isConnected = false
        private set(value) {
            field = value

            if (!value) {
                // Разрываем запросы, на которые ожидаем ответа
                requestsCallbacks.forEach {
                    it.value.subjectEmitter.onError(IOException("Connection not installed!"))
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
                SudoxApiStatus.DISCONNECTED
            })
        }

    init {
        connection.listener = this
    }

    override fun startConnection() {
        connection.start("sudox.ru", 5000)
    }

    override fun endConnection() {
        connection.end()
    }

    override fun <T : Any> sendRequest(
            methodName: String,
            requestData: Any,
            responseClass: Class<T>
    ): Single<T> = SingleSubject.create<T> {
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
            }
        } else {
            releaseRequestQueue(methodName)
            it.onError(IOException("Connection not installed!"))
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
    }

    override fun onReceive(text: String) {
        var methodName: String? = null
        var emitter: SingleEmitter<*>? = null

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
            emitter = callback.subjectEmitter as SingleEmitter<Any>

            if (result == OK_ERROR_CODE) {
                val dataNode = response.required("data")

                if (!dataNode.isObject) {
                    releaseRequestQueue(methodName)
                    return
                }

                emitter.onSuccess(objectMapper.treeToValue(dataNode, callback.dataClass))
            } else {
                emitter.onError(ApiException(result))
            }

            releaseRequestQueue(methodName)
        } catch (ex: Exception) {
            if (methodName != null) {
                releaseRequestQueue(methodName)
            }

            emitter?.onError(ex)
        }
    }

    override fun onReceive(bytes: ByteArray) {
        // Nothing
    }
}