package ru.sudox.api

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.PublishSubject
import ru.sudox.api.common.OK_ERROR_CODE
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import ru.sudox.api.common.exceptions.ApiException
import ru.sudox.api.common.exceptions.AttackSuspicionException
import ru.sudox.api.connections.Connection
import ru.sudox.api.connections.ConnectionListener
import ru.sudox.api.entries.ApiCallback
import ru.sudox.api.entries.dtos.ApiRequestDTO
import java.io.IOException
import java.util.Stack
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
        private val connection: Connection,
        private val objectMapper: ObjectMapper
) : ConnectionListener, SudoxApi {

    private val endSemaphore = Semaphore(0)
    private val requestsSemaphores = LinkedHashMap<String, Semaphore>()
    private val requestsCallbacks = LinkedHashMap<String, ApiCallback<*>>()
    private var updatesCallbacks = LinkedHashMap<String, Stack<ApiCallback<*>>>()

    override val statusSubject: PublishSubject<SudoxApiStatus> = PublishSubject.create()
    override var isConnected = false
        set(value) {
            field = value

            if (!value) {
                // Разрываем запросы, которые ожидают выполнения себе подобных
                requestsSemaphores.forEach { (_, semaphore) -> semaphore.release(semaphore.queueLength) }
                requestsCallbacks.forEach { throwException(it.value.observableEmitter, IOException("Connection not installed!")) }
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

        if (BuildConfig.DEBUG) {
            Log.d("Sudox API", "Start installing connection ...")
        }

        connection.start("sudox.ru", 5000)
    }

    override fun endConnection() {
        if (BuildConfig.DEBUG) {
            Log.d("Sudox API", "Closing connection ...")
        }

        connection.close()
        endSemaphore.acquire()
    }

    override fun <T : Any> sendRequest(methodName: String, requestData: Any, responseClass: Class<T>) = Observable.create<T> {
        if (isConnected) {
            acquireRequestQueue(methodName)

            // Проверим статус ещё раз, т.к. соединение могло быть разорвано во время ожидания очереди
            if (isConnected) {
                if (!it.isDisposed) {
                    requestsCallbacks[methodName] = ApiCallback<T>(it, responseClass)

                    val request = ApiRequestDTO(methodName, requestData)
                    val serialized = objectMapper.writeValueAsBytes(request)

                    connection.send(serialized)

                    if (BuildConfig.DEBUG) {
                        Log.d("Sudox API", "Request sent to server.")
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d("Sudox API", "Request abandoned because sender revoke it.")
                    }

                    releaseRequestQueue(methodName)
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d("Sudox API", "Request abandoned because connection was closed while queue waiting.")
                }

                releaseRequestQueue(methodName)
                throwException(it, IOException("Connection not installed!"))
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d("Sudox API", "Request abandoned because connection not installed.")
            }

            releaseRequestQueue(methodName)
            throwException(it, IOException("Connection not installed!"))
        }
    }

    override fun <T : Any> listenUpdate(updateName: String, dataClass: Class<T>) = Observable.create<T> {
        updatesCallbacks
                .getOrPut(updateName, { Stack() })
                .add(ApiCallback(it, dataClass))
    }

    override fun onStart() {
        isConnected = true

        if (BuildConfig.DEBUG) {
            Log.d("Sudox API", "Connected with server.")
        }
    }

    override fun onReceive(bytes: ByteArray) {
        val responseTree = objectMapper.readTree(bytes)
        val updateNameNode = responseTree.get("update_name")
        val methodNameNode = responseTree.get("method_name")
        val dataNode = responseTree.get("data")

        if (updateNameNode != null && updateNameNode.isTextual) {
            val iterator = updatesCallbacks[updateNameNode.asText()]?.iterator()

            if (iterator != null) {
                while (iterator.hasNext()) {
                    val next = iterator.next()

                    @Suppress("UNCHECKED_CAST")
                    val emitter = next.observableEmitter as ObservableEmitter<Any>

                    if (!emitter.isDisposed) {
                        @Suppress("RedundantUnitExpression")
                        emitter.onNext(if (dataNode != null) {
                            objectMapper.treeToValue(dataNode, next.dataClass)
                        } else {
                            Unit
                        })

                        emitter.onComplete()
                    } else {
                        iterator.remove()

                        if (BuildConfig.DEBUG) {
                            Log.d("Sudox API", "Removed disposed update listener.")
                        }
                    }
                }
            } else if (BuildConfig.DEBUG) {
                Log.d("Sudox API", "Server sent unwanted update.")
            }
        } else if (methodNameNode != null && methodNameNode.isTextual) {
            val methodName = methodNameNode.textValue()
            val resultNode = responseTree.get("method_result")
            val callback = requestsCallbacks[methodName]

            if (callback == null) {
                if (BuildConfig.DEBUG) {
                    Log.d("Sudox API", "Server sent unwanted response.")
                }
            } else if (resultNode == null || !resultNode.isInt) {
                if (BuildConfig.DEBUG) {
                    Log.d("Sudox API", "Result code isn't integer.")
                }

                releaseRequestQueue(methodName)
                throwException(callback.observableEmitter, AttackSuspicionException())
            } else {
                releaseRequestQueue(methodName)

                @Suppress("UNCHECKED_CAST")
                val emitter = callback.observableEmitter as ObservableEmitter<Any>

                if (!emitter.isDisposed) {
                    val result = resultNode.intValue()

                    @Suppress("RedundantUnitExpression")
                    if (result == OK_ERROR_CODE) {
                        @Suppress("RedundantUnitExpression")
                        emitter.onNext(if (dataNode != null) {
                            objectMapper.treeToValue(dataNode, callback.dataClass)
                        } else {
                            Unit
                        })

                        emitter.onComplete()
                    } else {
                        throwException(emitter, ApiException(result))
                    }
                } else if (BuildConfig.DEBUG) {
                    Log.d("Sudox API", "Request response ignored because sender revoke it.")
                }
            }
        } else if (BuildConfig.DEBUG) {
            Log.d("Sudox API", "Failed to recognize request type")
        }
    }

    override fun onClosed(throwable: Throwable?) {
        isConnected = false

        if (BuildConfig.DEBUG) {
            if (throwable != null) {
                Log.d("Sudox API", "Connection closed by error.", throwable)
            } else {
                Log.d("Sudox API", "Connection closed by user.")
            }
        }

        endSemaphore.release()
    }

    private fun releaseRequestQueue(methodName: String) {
        requestsSemaphores[methodName]?.let {
            it.release()

            if (it.queueLength == 0) {
                requestsSemaphores.remove(methodName)
                requestsCallbacks.remove(methodName)

                if (BuildConfig.DEBUG) {
                    Log.d("Sudox API", "Queue of $methodName method was ended.")
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d("Sudox API", "Queue length of $methodName method: ${it.queueLength}")
                }
            }
        }
    }

    private fun acquireRequestQueue(methodName: String) {
        var methodSemaphore = requestsSemaphores[methodName]

        if (methodSemaphore == null) {
            methodSemaphore = Semaphore(1)
            requestsSemaphores[methodName] = methodSemaphore

            if (BuildConfig.DEBUG) {
                Log.d("Sudox API", "Created queue for $methodName method")
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d("Sudox API", "Waiting $methodName method queue ...")
        }

        methodSemaphore.acquire()
    }

    private fun throwException(emitter: ObservableEmitter<out Any>, exception: Exception) {
        if (!emitter.isDisposed) {
            emitter.onError(exception)
            emitter.onComplete()
        }
    }
}