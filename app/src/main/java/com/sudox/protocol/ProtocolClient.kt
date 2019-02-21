package com.sudox.protocol

import android.util.Base64
import com.sudox.protocol.helpers.encryptAES
import com.sudox.protocol.helpers.getHmac
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.helpers.toJsonArray
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
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

@Singleton
class ProtocolClient @Inject constructor() {

    internal var socket: Socket? = null
    internal var controller: ProtocolController? = null
    private var reader: ProtocolReader? = null
    private var writer: ProtocolWriter? = null
    val readCallbacks = ConcurrentLinkedQueue<ReadCallback<*>>()
    private val errorsMessagesCallbacks = ArrayList<(Int) -> (Unit)>()
    val connectionStateChannel by lazy { ConflatedBroadcastChannel<ConnectionState>() }

    companion object {
        var VERSION: String = "0.5.0"
    }

    /**
     * Метод для установки соединения с сервером.
     *
     * Если соединение уже установлено, то ничего не произойдет.
     * Если соединение не установлено, но потоки включены, то произойдет их отключение и перезапуск вместе с соединением.
     */
    fun connect(notifyAboutError: Boolean = true) {
        kill(notifyAboutError)

        // Запускаем контроллер если он выключен.
        if (controller == null) controller = ProtocolController(this)

        // Подключение выполняем в потоке контроллера.
        controller!!.looperPreparedCallback = {
            socket = Socket()
            socket!!.keepAlive = false // У нас есть свой Ping-pong.

            // Устанавливаем соединение
            try {

                socket!!.connect(InetSocketAddress("api.sudox.ru", 5000), 5000)

                // Запускаем потоки чтения/записи.
                startThreads()

                // Рукопожатие.
                controller!!.onStart()
            } catch (e: IOException) {
                // Костыль, но должно работать
                if (notifyAboutError) GlobalScope.launch {
                    connectionStateChannel.offer(ConnectionState.CONNECT_ERRORED)
                }

                if (controller != null && controller!!.handler != null) {
                    controller!!.handler!!.removeCallbacksAndMessages(null)
                    controller!!.handler!!.postDelayed({ connect(false) }, 1000)
                }
            }
        }

        if (!controller!!.isAlive) {
            controller!!.start()
        } else {
            controller?.handler?.post { controller!!.looperPreparedCallback?.let { it() } }
        }
    }

    private fun startThreads() {
        if (reader == null) reader = ProtocolReader(this)
        if (writer == null) writer = ProtocolWriter(this)

        // Запуск.
        if (!reader!!.isAlive || reader!!.isInterrupted) reader!!.start()
        if (!writer!!.isAlive || writer!!.isInterrupted) writer!!.start()
    }

    /**
     * Метод для проверки правильности работы сокета и прочих потоков.
     **/
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

    fun isWorking(): Boolean {
        return controller != null
                && controller!!.isAlive
                && !controller!!.isInterrupted
    }

    /**
     * Метод для отключения всех потоков, связанных с текущим сокетом.
     * Также отключает сам сокет если он открыт.
     *
     * Нужен для безопасной остановки соединения.
     **/
    fun kill(killController: Boolean = true) {
        if (reader != null && controller != null && (!reader!!.isInterrupted || reader!!.isAlive)) {
            reader!!.interrupt()
            reader = null
        }

        if (controller != null && (!controller!!.isInterrupted || controller!!.isAlive) && killController) {
            controller!!.interrupt()
            controller = null
        }

        if (writer != null && controller != null && (!controller!!.isInterrupted || controller!!.isAlive)) {
            writer!!.interrupt()
            writer = null
        }

        if (socket != null && (socket!!.isConnected || !socket!!.isClosed)) socket!!.close()

        // Убираем ключ.
        if (controller != null) controller!!.key = null

        // "Убиваем" корутины (Если по-культурному, то снимаем блокировки и возвращаем NetworkException)
        notifyConnectionDestroyed()
    }

    /**
     * Закрывает соединение (сокет).
     **/
    fun close() {
        if (socket != null && socket!!.isConnected) socket!!.close()
    }

    fun sendArray(vararg params: Any) = sendString(params.toJsonArray().toString())

    /**
     * Методы для "внешней" отправки (на уровне сети, без шифрования)
     **/
    fun sendString(string: String) {
        try {
            writer!!.messagesQueue.put(string)
        } catch (e: InterruptedException) {
            writer!!.messagesQueue = LinkedBlockingQueue()
            writer!!.messagesQueue.put(string)
        }
    }

    fun sendMessage(event: String, message: JsonModel? = null) {
        if (!isValid()) {
            GlobalScope.launch {
                connectionStateChannel.offer(ConnectionState.CONNECTION_CLOSED)
            }
        } else {
            controller!!.handler!!.post(object : Runnable {
                override fun run() {
                    if (isValid()) {
                        val key = controller!!.key

                        if (key != null) {
                            val iv = randomBase64String(16)
                            val salt = randomBase64String(32)
                            val jsonArray = message?.toJSONArray()
                            val jsonObject = message?.toJSON() ?: JSONObject()
                            val json = (jsonArray ?: jsonObject)
                            val message = arrayOf(event, json)
                            val payload = arrayOf(message, salt)
                                    .toJsonArray()
                                    .toString()
                                    .replace("\\/", "/")

                            val hmac = Base64.encodeToString(
                                    getHmac(controller!!.key!!, payload),
                                    Base64.NO_WRAP)

                            // Шифруем данные ...
                            val encryptedPayload = encryptAES(controller!!.key!!, iv, payload)

                            println("Sudox ------------")
                            println("Sudox Payload: $payload")
                            println("Sudox Payload: $hmac")

                            // Отправим массив данных.
                            sendArray("msg", iv, encryptedPayload, hmac)
                        } else {
                            controller!!.handler?.postDelayed(this, 1000)
                        }
                    } else {
                        GlobalScope.launch { connectionStateChannel.offer(ConnectionState.CONNECTION_CLOSED) }
                    }
                }
            })
        }
    }

    /**
     * Добавляет кэллбэк в список.
     *
     * Метод публичный, т.к. используется в inline-методах.
     * Ни в коем случае не использовать вне протокола!
     *
     * Асинхронный, т.к. дурак может выполнить его в UI-потоке,
     * а как вы знаете, я против любых операций, кроме операций с дизайном в UI потоке...
     */
    fun <T : JsonModel> addToCallbacks(event: String?, clazz: KClass<T>, resultFunction: (T) -> (Unit), once: Boolean) {
        readCallbacks.plusAssign(ReadCallback(
                resultFunction = resultFunction,
                clazz = clazz,
                event = event,
                once = once
        ))
    }

    /**
     * Подписывает кэллбэк на постоянное получение данных. Не будет даже после разрыва/потери соединения.
     */
    inline fun <reified T : JsonModel> listenMessage(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(event, T::class, resultFunction, false)
    }

    /**
     * Подписывает кэллбэк на единственное получение данных.
     * Все "одноразовые" кэллбэки, которые не получили данные, будут удалены после разрыва/потери соединения.
     */
    inline fun <reified T : JsonModel> listenMessageOnce(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(event, T::class, resultFunction, true)
    }

    /**
     * Подписывает кэллбэк на получение ошибок во всех сообщениях.
     */
    fun listenErrorCodes(resultFunction: (Int) -> (Unit)) {
        errorsMessagesCallbacks.plusAssign(resultFunction)
    }

    /**
     * Подписывает кэллбэк на единственный ответ, шифрует и отправляет сообщение на сервер.
     */
    inline fun <reified T : JsonModel> makeRequest(event: String, message: JsonModel? = null, noinline resultFunction: (T) -> (Unit)) {
        listenMessageOnce(event, resultFunction)
        sendMessage(event, message)
    }

    inline fun <reified T : JsonModel> makeRequest(event: String, message: JsonModel? = null) = GlobalScope.async(Dispatchers.IO) {
        return@async suspendCoroutine<T> { coroutine ->
            readCallbacks.plusAssign(ReadCallback(
                    coroutine = coroutine,
                    clazz = T::class,
                    event = event,
                    once = true,
                    notifyAboutConnectionDestroyed = false
            ))

            sendMessage(event, message)
        }
    }

    inline fun <reified T : JsonModel> makeRequestWithControl(event: String, message: JsonModel? = null) = GlobalScope.async(Dispatchers.IO) {
        return@async suspendCoroutine<T> { coroutine ->
            readCallbacks.plusAssign(ReadCallback(
                    coroutine = coroutine,
                    clazz = T::class,
                    event = event,
                    once = true,
                    notifyAboutConnectionDestroyed = true
            ))

            sendMessage(event, message)
        }
    }

    /**
     * Метод, передающий в callback'и указанного эвента пришедшую информацию с сервера.
     * Предварительно выполняет чтение и парсинг JSON.
     */
    internal fun notifyCallbacks(event: String, json: String) = GlobalScope.launch(Dispatchers.IO) {
        val iterator = readCallbacks.iterator()

        /* Тут будет выгоднее использовать Iterator, т.к. при его использовании мы можем удалить
           обьект из списка в цикле, не боясь ConcurrentModificationException. */
        while (iterator.hasNext()) {
            val next = iterator.next()
            val instance = next.clazz.java.newInstance()
            val once = next.coroutine != null || next.once

            // Оперируем с кэллбэком
            if (next.event != event) continue
            if (once) iterator.remove()

            // Загоним в него данные
            instance.readResponse(JSONObject(json))

            // Ошибка...
            if (instance.containsError()) errorsMessagesCallbacks.forEach { it(instance.error) }

            // Крикнем в окно (для IDEA: не ори на отсуствие проверку типов)
            @Suppress("UNCHECKED_CAST")
            (if (next.coroutine != null) {
                (next.coroutine as Continuation<JsonModel>).resume(instance)
            } else {
                (next.resultFunction as (JsonModel) -> (Unit))(instance)
            })

            if (once) break
        }
    }

    /**
     * Метод, разблокирующий корутины при обрыве соединения.
     * Возвращает NetworkException в корутины.
     */
    private fun notifyConnectionDestroyed() {
        val iterator = readCallbacks.iterator()

        /* Тут будет выгоднее использовать Iterator, т.к. при его использовании мы можем удалить
           обьект из списка в цикле, не боясь ConcurrentModificationException. */
        while (iterator.hasNext()) {
            val next = iterator.next()

            // Пока и всегда только корутины ;)
            if (next.coroutine != null && next.notifyAboutConnectionDestroyed) {
                next.coroutine.resumeWithException(NetworkException())
                iterator.remove()
            }
        }
    }
}