package com.sudox.api

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
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
class SudoxApi(
        val connection: Connection,
        val objectMapper: ObjectMapper
) : ConnectionListener {

    val requestsSemaphores = LinkedHashMap<String, Semaphore>()
    val requestsCallbacks = LinkedHashMap<String, ApiRequestCallback<*>>()
    val statusSubject: PublishSubject<SudoxApiStatus> = PublishSubject.create()
    var isConnected = false
        private set(value) {
            field = value

            statusSubject.onNext(if (value) {
                SudoxApiStatus.CONNECTED
            } else {
                SudoxApiStatus.DISCONNECTED
            })
        }

    init {
        connection.listener = this
    }

    /**
     * Запускает соединение с сервером.
     */
    fun startConnection() {
        connection.start("sudox.ru", 5000)
    }

    /**
     * Останавливает соединение с сервером.
     */
    fun endConnection() {
        connection.end()
    }

    /**
     * Отправляет запрос на сервер.
     *
     * @param methodName Название вызываемого метода.
     * @param data Данные для запроса.
     */
    inline fun <reified T : Any> sendRequest(methodName: String, data: Any): Single<T> = SingleSubject.create<T> {
        if (isConnected) {
            acquireRequestSemaphore(methodName)

            if (isConnected) {
                requestsCallbacks[methodName] = ApiRequestCallback<T>(it, T::class.java)

                val request = ApiRequest(methodName, data)
                val serialized = objectMapper.writeValueAsString(request)

                connection.send(serialized)
            } else {
                it.onError(IOException("Connection not installed!"))
                releaseRequestSemaphore(methodName)
            }
        } else {
            it.onError(IOException("Connection not installed!"))
            releaseRequestSemaphore(methodName)
        }
    }

    /**
     * Разблокировывает очередь запросов с определенным методом.
     *
     * @param methodName Название метода.
     */
    fun releaseRequestSemaphore(methodName: String) {
        requestsSemaphores[methodName]?.let {
            it.release()

            if (it.queueLength == 0) {
                requestsSemaphores.remove(methodName)
            }
        }
    }

    /**
     * Блокирует очередь запросов с определенным методом.
     *
     * @param methodName Название метода.
     */
    fun acquireRequestSemaphore(methodName: String) {
        requestsSemaphores
                .getOrPut(methodName, { Semaphore(1) })
                .acquire()
    }

    override fun onStart() {
        isConnected = true
    }

    override fun onReceive(bytes: ByteArray) {
    }

    override fun onReceive(text: String) {
        try {
            val response = objectMapper.readTree(text)
            val resultNode = response.required("result")

            if (!resultNode.isInt) {
                Log.e("Sudox API", "Error during deserialization: $text")
                return
            }

            val methodNameNode = response.required("method_name")

            if (!methodNameNode.isTextual) {
                Log.e("Sudox API", "Error during deserialization: $text")
                return
            }

            val result = resultNode.intValue()
            val methodName = methodNameNode.textValue()
            val callback = requestsCallbacks[methodName] ?: return

            @Suppress("UNCHECKED_CAST")
            val emitter = callback.subjectEmitter as SingleEmitter<Any>

            if (result == OK_ERROR_CODE) {
                val dataNode = response.required("data")

                if (!dataNode.isObject) {
                    Log.e("Sudox API", "Error during deserialization: $text")
                    releaseRequestSemaphore(methodName)
                    return
                }

                emitter.onSuccess(objectMapper.treeToValue(dataNode, callback.dataClass))
            } else {
                emitter.onError(ApiException(result))
            }

            releaseRequestSemaphore(methodName)
        } catch (ex: Exception) {
            Log.e("Sudox API", "Error during deserialization: $text")
        }
    }

    override fun onEnd() {
        isConnected = false
    }
}