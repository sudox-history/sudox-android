package com.sudox.protocol

import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.ReadCallback
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Обьект для работы с протоколом Sudox.
 *
 * Выполняет функцию обработки, отправки запросов, доставки данных
 * до их получателя в коде. Контроллирует соединение сокета с сервером,
 * уведомляет слушателей об изменении его состояния.
 */
class ProtocolClient {

    @JvmField
    internal var socket: Socket? = null
    @JvmField
    internal var controller: ProtocolController? = null

    // Потоки ввода/вывода
    private var reader: ProtocolReader? = null
    private var writer: ProtocolWriter? = null

    // Кэллбэки
    @JvmField
    var readCallbacks = ConcurrentLinkedDeque<ReadCallback<*>>()
    @JvmField
    var errorsMessagesCallbacks = ArrayList<(Int) -> (Unit)>()
    @JvmField
    var connectionStateChannel = ConflatedBroadcastChannel<ConnectionState>()

    companion object {
        var VERSION: String = "0.5.1"
    }

    /**
     * Метод для установки соединения с сервером.
     * Если соединение уже установлено, то ничего не произойдет.
     * Если соединение не установлено, но потоки включены, то произойдет их отключение и перезапуск вместе с соединением.
     *
     * @param notifyAboutError - нужно ли уведомлять об ошибке во время подключения?
     */
    fun connect(notifyAboutError: Boolean = true) {
        kill(notifyAboutError)

        // Init controller
        if (controller == null) {
            controller = ProtocolController(this).apply {
                looperPreparedCallback = {
                    try {
                        socket = Socket()
                        socket!!.keepAlive = false
                        socket!!.tcpNoDelay = false

                        // Connect ...
                        socket!!.connect(InetSocketAddress("46.173.214.49", 5000), 5000)

                        // Start controller & IO threads
                        startThreads()

                        // Start handshake ...
                        controller!!.onStart()
                    } catch (e: IOException) {
                        if (notifyAboutError) connectionStateChannel.offer(ConnectionState.CONNECT_ERROR)

                        // Removed delayed tasks & post connect task ...
                        controller?.handler?.removeCallbacksAndMessages(null)
                        controller?.handler?.postDelayed({ connect(false) }, 1000)
                    }
                }
            }
        }

        // Reuse controller
        if (controller!!.isAlive) {
            controller?.handler?.removeCallbacksAndMessages(null)
            controller?.handler?.post { controller!!.looperPreparedCallback?.invoke() }
        } else {
            controller!!.start()
        }
    }

    /**
     * Запускает потоки чтения/записи.
     */
    private fun startThreads() {
        if (reader == null) reader = ProtocolReader(this)
        if (writer == null) writer = ProtocolWriter(this)

        // Запуск.
        if (!reader!!.isAlive || reader!!.isInterrupted) reader!!.start()
        if (!writer!!.isAlive || writer!!.isInterrupted) writer!!.start()
    }

    /**
     * Метод, проверяющий работу протокола.
     * Протокол работает правильно если:
     *
     * 1) Запущен контроллер протокола;
     * 2) Запущен сокет вместе с соединением к серверу;
     * 3) Запущены потоки записи и чтения.
     */
    fun isValid(): Boolean {
        return controller != null
                && controller!!.isAlive
                && !controller!!.isInterrupted
                && socket != null
                && socket!!.isConnected
                && reader != null
                && writer != null
                && reader!!.isAlive
                && writer!!.isAlive
                && !reader!!.isInterrupted
                && !writer!!.isInterrupted
    }

    /**
     * Проверяет статус контроллера протокола.
     */
    fun isWorking() = controller != null && controller!!.isAlive && !controller!!.isInterrupted

    /**
     * Метод для отключения всех потоков, связанных с текущим сокетом.
     * Нужен для безопасной остановки соединения.
     *
     * @param killController - нужно ли отключать контроллер?
     */
    fun kill(killController: Boolean = true) {
        // Выключаем поток чтения.
        if (reader != null && controller != null && (!reader!!.isInterrupted || reader!!.isAlive)) {
            reader!!.interrupt()
            reader = null
        }

        // Выключаем контроллер.
        if (controller != null && (!controller!!.isInterrupted || controller!!.isAlive) && killController) {
            controller!!.interrupt()
            controller = null
        }

        // Выключаем поток записи.
        if (writer != null && controller != null && (!controller!!.isInterrupted || controller!!.isAlive)) {
            writer!!.interrupt()
            writer = null
        }

        // Закрываем сокет.
        if (socket != null && (socket!!.isConnected || !socket!!.isClosed)) {
            socket!!.close()
        }

        // "Убиваем" корутины (Если по-культурному, то снимаем блокировки и возвращаем NetworkException)
        notifyConnectionDestroyed()
    }

    /**
     * Закрывает соединение (сокет).
     */
    fun close() {
        if (socket != null && socket!!.isConnected) {
            socket!!.close()
        }
    }

    /**
     * Отправляет массив данных в формате JSON по незащищенному каналу.
     *
     * @param params - последовательность данных для отправки.
     */
    internal fun sendArray(vararg params: Any) = sendString(JSONArray(params).toString())

    /**
     * Отправляет строку по незащищенному каналу.
     *
     * @param string - строка, которую нужно отправить.
     */
    private fun sendString(string: String) {
        writer!!.addToQueue(string.toByteArray())
    }

    /**
     * Отправляет сообщения в формате JSON по защищенному каналу.
     *
     * @param event - название события для отправки.
     * @param message - обьект сообщения для отправки.
     */
    fun sendJsonMessage(event: String, message: JsonModel? = null) {
        if (!isWorking()) {
            connectionStateChannel.offer(ConnectionState.NO_CONNECTION)
        } else {
            controller!!.sendJsonMessage(event, message)
        }
    }

    /**
     * Добавляет кэллбэк в список.
     *
     * Метод публичный, т.к. используется в inline-методах.
     * Ни в коем случае не использовать вне протокола!
     *
     * @param readCallback - обьект для обработки кэллбэка.
     */
    fun <T : JsonModel> addToCallbacks(readCallback: ReadCallback<T>?) {
        readCallbacks.plusAssign(readCallback)
    }

    /**
     * Подписывает кэллбэк на постоянное получение данных. Не будет отключен даже после разрыва/потери соединения.
     *
     * @param event - название прослушиваемого события.
     * @param resultFunction - функция для обратного вызова.
     */
    inline fun <reified T : JsonModel> listenMessage(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(ReadCallback(
                resultFunction = resultFunction,
                clazz = T::class,
                event = event,
                once = false
        ))
    }

    /**
     * Подписывает кэллбэк на единственное получение данных.
     * Все "одноразовые" кэллбэки, которые не получили данные, будут удалены после разрыва/потери соединения.
     *
     * @param event - название прослушиваемого события.
     * @param resultFunction - функция для обратного вызова.
     */
    inline fun <reified T : JsonModel> listenMessageOnce(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(ReadCallback(
                resultFunction = resultFunction,
                clazz = T::class,
                event = event,
                once = true
        ))
    }

    /**
     * Подписывает кэллбэк на получение ошибок во всех сообщениях.
     *
     * @param resultFunction - функция для обратного вызова.
     */
    fun listenErrorCodes(resultFunction: (Int) -> (Unit)) {
        errorsMessagesCallbacks.plusAssign(resultFunction)
    }

    /**
     * Отправляет запрос на сервер в формате JSON по защищенному каналу.
     * Кидает NetworkException в случае отсутствия соединения.
     *
     * @param event - событие, которое нужно отправить и ожидать в качестве ответа.
     * @param message - сообщение, которое нужно отправить.
     */
    @Throws(NetworkException::class)
    inline fun <reified T : JsonModel> makeRequest(event: String, message: JsonModel? = null) = GlobalScope.async(Dispatchers.IO) {
        return@async suspendCoroutine<T> { coroutine ->
            readCallbacks.plusAssign(ReadCallback(
                    coroutine = coroutine,
                    clazz = T::class,
                    event = event,
                    once = true,
                    notifyAboutConnectionDestroyed = true
            ))

            sendJsonMessage(event, message)
        }
    }

    /**
     * Метод, передающий в callback'и указанного эвента пришедшую информацию с сервера.
     * Предварительно выполняет чтение и парсинг JSON.
     *
     * @param event - пришедшее событие.
     * @param json - пришедшие данные.
     */
    internal fun notifyCallbacks(event: String, json: String) = GlobalScope.launch(Dispatchers.IO) {
        val iterator = readCallbacks.iterator()

        // Тут будет выгоднее использовать Iterator, т.к. при его использовании мы можем удалить
        // обьект из списка в цикле, не боясь ConcurrentModificationException.
        while (iterator.hasNext()) {
            val next = iterator.next()
            val instance = next.clazz.java.newInstance()
            val coroutine = next.coroutine
            val once = coroutine != null || next.once

            // Оперируем с кэллбэком
            if (next.event != event) continue
            if (once) iterator.remove()

            // Загоним в него данные
            instance.readResponse(JSONObject(json))

            // Ошибка...
            if (!instance.isSuccess()) errorsMessagesCallbacks.forEach { it(instance.error) }

            // Крикнем в окно (для IDEA: не ори на отсуствие проверку типов)
            @Suppress("UNCHECKED_CAST")
            (if (coroutine != null) {
                (coroutine as Continuation<JsonModel>).resume(instance)
            } else {
                (next.resultFunction as (JsonModel) -> (Unit))(instance)
            })

            if (once) break
        }
    }

    /**
     * Разблокирует корутины, возвращая в них NetworkException.
     */
    private fun notifyConnectionDestroyed() {
        val iterator = readCallbacks.iterator()

        // Тут будет выгоднее использовать Iterator, т.к. при его использовании мы можем удалить
        // обьект из списка в цикле, не боясь ConcurrentModificationException.
        while (iterator.hasNext()) {
            val next = iterator.next()
            val coroutine = next.coroutine

            // Пока и всегда только корутины ;)
            if (coroutine != null && next.notifyAboutConnectionDestroyed) {
                coroutine.resumeWithException(NetworkException())
                iterator.remove()
            }
        }
    }
}