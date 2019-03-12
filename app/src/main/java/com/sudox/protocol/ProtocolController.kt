package com.sudox.protocol

import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.util.Base64
import com.sudox.android.data.models.core.CoreVersionDTO
import com.sudox.protocol.helpers.*
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import java.security.interfaces.ECPublicKey
import java.security.spec.InvalidKeySpecException

/**
 * Контроллер протокола (соединения).
 *
 * Выполняет функцию потока, принимает пакеты, обрабатывает их отправку.
 * Контроллирует статус соединения, выполняет соединение с сервером.
 * Может восстанавливать соединение с сервером.
 **/
class ProtocolController(private val client: ProtocolClient) : HandlerThread("SSTP Controller") {

    // Для взаимодействия с другими потоками.
    internal var handler: Handler? = null
    internal var key: ByteArray? = null
    internal var alive: Boolean = false

    // Looper prepared callback
    internal var looperPreparedCallback: (() -> (Unit))? = null
    internal var getCoreVersionJob: Job? = null

    // Создаем обработчик.
    override fun onLooperPrepared() {
        handler = Handler(looper)

        // Инициализация Looper'а
        if (looperPreparedCallback != null) looperPreparedCallback!!()
    }

    /**
     * Проверяет, выполнено ли рукопожатие.
     **/
    private fun handshakeIsInvoked() = key != null

    /**
     * Вызывается когда соединение будет успешно установлено.
     **/
    fun onStart() {
        // Соединение активно (нужно для функционирования ping-pong'а)
        alive = true

        // Начинаем хэндшейк
        client.sendArray("vrf") // Начинаем Handshake ...
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
                "png" -> if (alive) client.sendArray("png") else alive = true
            }

            // Remove all old tasks
            handler!!.removeCallbacksAndMessages(0)
            handler!!.removeCallbacksAndMessages(1)

            // Plane tasks
            handler!!.postAtTime({
                alive = false

                // Send ping
                client.sendArray("png")

                // Отправляем пинг
                handler!!.postAtTime({ if (!alive) client.close() }, 1, SystemClock.uptimeMillis() + 2000L)
            }, 0, SystemClock.uptimeMillis() + 6000L)
        } catch (e: JSONException) {
            if (!handshakeIsInvoked()) client.close()
        } catch (e: InvalidKeySpecException) {
            if (!handshakeIsInvoked()) client.close()
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["vrf"]
     */
    private fun handleVerify(packet: JSONArray) {
        if (packet.length() >= 3) {
            val serverEcdhPublicKey = packet.optString(1)
            val serverEcdhPublicKeySignature = packet.optString(2)

            if (serverEcdhPublicKey != null
                    && serverEcdhPublicKeySignature != null
                    && serverEcdhPublicKey.matches(BASE64_REGEX)
                    && serverEcdhPublicKeySignature.matches(BASE64_REGEX)
                    && verifyData(serverEcdhPublicKey, serverEcdhPublicKeySignature)) {

                // Генерируем пару ключей (публичный и приватный)
                val keyPair = generateKeys()

                // Prepare public key
                val publicKeyBytes = (keyPair.public as ECPublicKey).getPoint()
                val encodedPublicKey = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)

                // Generate secret key
                val serverEcdhPublicKeyInstance = readPublicKey(serverEcdhPublicKey)
                val secretKey = calculateSecretKey(keyPair.private, serverEcdhPublicKeyInstance)
                        .copyOf(24)

                val secretHash = Base64.encodeToString(calculateHash(secretKey), Base64.NO_WRAP)

                // Encrypt
                key = secretKey

                // Send data to the server ...
                client.sendArray("upg", encodedPublicKey, secretHash)
            } else client.close()
        } else client.close()
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
                    .makeRequestWithControl<CoreVersionDTO>("core.getVersion")
                    .await()

            // Task can be invalidated
            if (!isActive) return@async

            // Compare versions
            if (coreVersionDTO.version == ProtocolClient.VERSION) {
                client.connectionStateChannel.offer(ConnectionState.HANDSHAKE_SUCCEED)
            } else {
                client.connectionStateChannel.offer(ConnectionState.OLD_PROTOCOL_VERSION)
                client.close()
                client.kill()
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["msg"]
     **/
    private fun handleMessage(packet: JSONArray) {
        if (packet.length() >= 4) {
            val iv = packet.optString(1)
            val payload = packet.optString(2)
            val hmac = packet.optString(3)

            if (iv != null
                    && payload != null
                    && hmac != null
                    && iv.matches(BASE64_REGEX)
                    && payload.matches(BASE64_REGEX)
                    && hmac.matches(BASE64_REGEX)) {

                val decryptedPayloadString = decryptAES(key!!, iv, payload)
                        .replace("\\/", "/")
                val decryptedPayload = JSONArray(decryptedPayloadString)

                // Check length
                if (decryptedPayload.length() >= 3) {
                    val event = decryptedPayload.optString(0)
                    val message = decryptedPayload.optJSONObject(1)
                    val salt = decryptedPayload.optString(2)

                    if (event != null && message != null && salt != null && key != null) {
                        val hmacReaded = calculateHMAC(key!!, decryptedPayloadString)
                        val encodedHmac = Base64.encodeToString(hmacReaded, Base64.NO_WRAP)

                        // Проверим HMAC'ки ...
                        if (encodedHmac == hmac) client.notifyCallbacks(event, message.toString())
                    }
                }
            }
        }
    }

    /**
     * Вызывается при окончании соединения.
     **/
    fun onEnd() = handler!!.post {
        handler!!.removeCallbacksAndMessages(null)

        // Close connection ...
        getCoreVersionJob?.cancel()
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