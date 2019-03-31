package com.sudox.protocol

import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import com.sudox.protocol.helpers.CipherHelper
import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.dto.CoreVersionDTO
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.spec.InvalidKeySpecException

/**
 * Контроллер протокола (соединения).
 *
 * Выполняет функцию потока, принимает пакеты, обрабатывает их отправку.
 * Контроллирует статус соединения, выполняет соединение с сервером.
 * Может восстанавливать соединение с сервером.
 *
 * @param client - клиент протокола.
 */
class ProtocolController(private val client: ProtocolClient) : HandlerThread("SSTP Controller") {

    // Для взаимодействия с другими потоками.
    internal var encryptionKey: ByteArray? = null
    internal var handler: Handler? = null
    internal var looperPreparedCallback: (() -> (Unit))? = null
    private var requestedPing: Boolean = false
    private var isHandshakeInvoked = false
    private var getCoreVersionJob: Job? = null

    companion object {
        const val PING_SEND_TASK_TOKEN = 0
        const val PING_CHECK_TASK_TOKEN = 1
        const val SEND_PING_BEFORE_PACKET_INTERVAL = 6000L
        const val CHECK_PING_BEFORE_SEND = 2000L
    }

    override fun onLooperPrepared() {
        handler = Handler(looper)

        // Инициализация Looper'а
        if (looperPreparedCallback != null) {
            looperPreparedCallback!!()
        }
    }

    /**
     * Вызывается когда соединение будет успешно установлено.
     **/
    fun onStart() {
        // Соединение активно (нужно для функционирования ping-pong'а)
        requestedPing = true
        isHandshakeInvoked = false
        encryptionKey = null

        // Начинаем хэндшейк
        client.sendArray("vrf")
    }

    /**
     * Вызывается при приходе пакета.
     **/
    fun onPacket(string: String) = handler!!.post {
        try {
            val packet = JSONArray(string)
            val type = packet.optString(0) ?: return@post

            // Ищем метод-обработчик
            when (type) {
                "vrf" -> handleVerify(packet)
                "upg" -> handleUpgrade(packet)
                "msg" -> handleMessage(packet)
                "png" -> handlePing()
            }

            // Reschedule ping tasks
            handler!!.removeCallbacksAndMessages(PING_SEND_TASK_TOKEN)
            handler!!.removeCallbacksAndMessages(PING_CHECK_TASK_TOKEN)
            handler!!.postAtTime(::sendPing, PING_SEND_TASK_TOKEN, SystemClock.uptimeMillis() + SEND_PING_BEFORE_PACKET_INTERVAL)
        } catch (e: JSONException) {
            if (!isHandshakeInvoked) client.close()
        } catch (e: InvalidKeySpecException) {
            if (!isHandshakeInvoked) client.close()
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["vrf"]
     */
    private fun handleVerify(packet: JSONArray) {
        if (packet.length() >= 3) {
            val serverPublicKeyEncoded = packet.optString(1)
            val serverPublicKeySignatureEncoded = packet.optString(2)

            if (serverPublicKeyEncoded != null
                    && serverPublicKeySignatureEncoded != null
                    && serverPublicKeyEncoded.matches(BASE64_REGEX)
                    && serverPublicKeySignatureEncoded.matches(BASE64_REGEX)) {

                val serverPublicKey = CipherHelper.decodeFromBase64(serverPublicKeyEncoded.toByteArray())
                val serverPublicKeySignature = CipherHelper.decodeFromBase64(serverPublicKeySignatureEncoded.toByteArray())

                // Preventing MITM-attacks ...
                if (CipherHelper.verifyMessageWithECDSA(serverPublicKey, serverPublicKeySignature)) {
                    val pairId = CipherHelper.generateKeysPair()
                    val publicKey = CipherHelper.getPublicKey(pairId)
                    val privateKey = CipherHelper.getPrivateKey(pairId)
                    val secretKey = CipherHelper.calculateSecretKey(privateKey, serverPublicKey)

                    // Preventing MITM-attacks ...
                    if (secretKey.isNotEmpty()) {
                        val encryptionKey = secretKey.copyOf(24)
                        val encryptionKeyHash = CipherHelper.calculateSHA224(encryptionKey)
                        val encryptionKeyHashEncoded = CipherHelper.encodeToBase64(encryptionKeyHash)
                        val publicKeyEncoded = CipherHelper.encodeToBase64(publicKey)

                        // Save encryption key ...
                        this.encryptionKey = encryptionKey

                        // Send data to the server ...
                        client.sendArray("upg", publicKeyEncoded, encryptionKeyHashEncoded)
                    } else client.close()
                } else client.close()
            } else client.close()
        } else client.close()
    }

    /**
     * Метод, вызываемый при приходе Ping'а.
     *
     * Обновляет статус прихода пакета.
     */
    private fun handlePing() {
        if (requestedPing) {
            client.sendArray("png")
        } else {
            requestedPing = true
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["upg"]
     **/
    private fun handleUpgrade(packet: JSONArray) {
        if (packet.length() >= 2) {
            val status = packet.optInt(1) == 1

            // Handshake failed!
            if (!status) {
                client.close()
                return
            }

            // Check protocol version ...
            getCoreVersionJob = compareVersions()
        }
    }

    /**
     * Сравнивает версии протоколов на сервере и в клиенте.
     *
     * Если все ОК, то отправляет в шину событий протокола - HANDSHAKE_SUCCEED
     * В противном случае отправит в шину событий протокола - OLD_PROTOCOL_VERSION
     */
    private fun compareVersions() = GlobalScope.async(Dispatchers.IO) {
        try {
            val coreVersionDTO = client
                    .makeRequest<CoreVersionDTO>("core.getVersion")
                    .await()

            // Task can be invalidated
            if (!isActive) return@async

            // Compare versions
            if (coreVersionDTO.version == ProtocolClient.VERSION) {
                isHandshakeInvoked = true
                client.connectionStateChannel.offer(ConnectionState.HANDSHAKE_SUCCEED)
            } else {
                isHandshakeInvoked = false
                encryptionKey = null
                client.connectionStateChannel.offer(ConnectionState.OLD_PROTOCOL_VERSION)
                client.close()
                client.kill()
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }
    }

    /**
     * Отправляет пакет Ping, запускает проверку на приход ответа.
     */
    private fun sendPing() {
        requestedPing = false

        // Отправляем Ping и проверяем приход ответа через некоторые время.
        client.sendArray("png")

        // Ожидаем ответ ...
        handler!!.postAtTime(::checkPing, PING_CHECK_TASK_TOKEN, SystemClock.uptimeMillis() + CHECK_PING_BEFORE_SEND)
    }

    /**
     * Проверяет приход ответа на Ping.
     * Если ответа нет - закрывает соединение.
     */
    private fun checkPing() {
        // Закрываем соединение если нет ответа ...
        if (!requestedPing) {
            client.close()
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["msg"]
     **/
    private fun handleMessage(packet: JSONArray) {
        if (encryptionKey != null) {
            if (packet.length() >= 4) {
                val ivEncoded = packet.optString(1)
                val payloadEncoded = packet.optString(2)
                val hmacEncoded = packet.optString(3)

                if (ivEncoded != null
                        && payloadEncoded != null
                        && hmacEncoded != null
                        && ivEncoded.matches(BASE64_REGEX)
                        && payloadEncoded.matches(BASE64_REGEX)
                        && hmacEncoded.matches(BASE64_REGEX)) {

                    val iv = CipherHelper.decodeFromBase64(ivEncoded.toByteArray())
                    val payload = CipherHelper.decodeFromBase64(payloadEncoded.toByteArray())
                    val decryptedPayload = CipherHelper.decryptWithAES(encryptionKey, iv, payload)
                    val decryptedPayloadArray = JSONArray(decryptedPayload)

                    // Preventing MITM-attacks
                    if (decryptedPayloadArray.length() >= 3) {
                        val event = decryptedPayloadArray.optString(0)
                        val message = decryptedPayloadArray.optJSONObject(1)
                        val salt = decryptedPayloadArray.optString(2)

                        if (event != null && message != null && salt != null && isHandshakeInvoked && encryptionKey != null) {
                            val hmacRead = CipherHelper.calculateHMAC(encryptionKey, decryptedPayload)
                            val hmacReadEncoded = CipherHelper.encodeToBase64(hmacRead)

                            // Checking HMAC's
                            if (hmacEncoded == hmacReadEncoded) {
                                client.notifyCallbacks(event, message.toString())
                            }
                        }
                    }
                }
            }
        } else client.close()
    }

    /**
     * Отправляет сообщение в формате JSON на сервер.
     *
     * @param event - название события для отправки.
     * @param message - обьект сообщения для отправки.
     */
    fun sendJsonMessage(event: String, message: JsonModel? = null) = handler!!.post {
        if (client.isValid() && isHandshakeInvoked && encryptionKey != null) {
            val salt = CipherHelper.generateBase64(8)
            val jsonObject = message?.toJSON()
            val jsonMessage = jsonObject ?: JSONObject()
            val payload = JSONArray(arrayOf(event, jsonMessage, salt))
                    .toString()
                    .replace("\\/", "/")
                    .toByteArray()

            // Encrypting ...
            val hmac = CipherHelper.calculateHMAC(encryptionKey, payload)
            val iv = CipherHelper.generateBytes(16)
            val hmacEncoded = CipherHelper.encodeToBase64(hmac)
            val ivEncoded = CipherHelper.encodeToBase64(iv)
            val payloadEncrypted = CipherHelper.encryptWithAES(encryptionKey, iv, payload)
            val payloadEncryptedEncoded = CipherHelper.encodeToBase64(payloadEncrypted)

            // Send packet to server
            client.sendArray("msg", ivEncoded, payloadEncryptedEncoded, hmacEncoded)
        } else {
            client.connectionStateChannel.offer(ConnectionState.NO_CONNECTION)
        }
    }

    /**
     * Вызывается при окончании соединения.
     **/
    fun onEnd() = handler!!.post {
        handler!!.removeCallbacksAndMessages(null)

        // Cancel all internal tasks ...
        getCoreVersionJob?.cancel()
        isHandshakeInvoked = false
        encryptionKey = null

        // Kill connection ...
        client.kill(false)
        client.connectionStateChannel.offer(ConnectionState.CONNECTION_CLOSED)

        // Post reconnect after 1000ms
        handler!!.postDelayed({ client.connect(false) }, 1000)
    }

    override fun interrupt() {
        handler?.removeCallbacksAndMessages(null)

        // Super!
        super.interrupt()
    }
}