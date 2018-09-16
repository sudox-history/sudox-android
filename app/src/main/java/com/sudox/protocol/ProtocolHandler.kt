package com.sudox.protocol

import android.util.Base64
import com.sudox.android.common.enums.ConnectState
import com.sudox.protocol.helpers.*
import org.json.JSONArray
import org.json.JSONException
import java.security.spec.InvalidKeySpecException
import javax.crypto.interfaces.DHPublicKey

class ProtocolHandler(private val client: ProtocolClient) {

    internal var key: ByteArray? = null

    fun handleStart() {
        client.sendArray(arrayOf("vrf"))
    }

    fun handlePacket(message: String) {
        try {
            val packet = message.toJsonArray()
            val type = packet.optString(0) ?: return

            when (type) {
                "vrf" -> handleVerify(packet)
                "upg" -> handleUpgrade(packet)
                "msg" -> handleMessage(packet)
            }
        } catch (e: JSONException) {
            // Ignore
        } catch (e: InvalidKeySpecException) {
            handleAttack()
        }
    }

    private fun handleVerify(packet: JSONArray) {
        if (packet.length() >= 3) {
            val serverPublicKey = packet.optString(1)
            val serverSignature = packet.optString(2)

            if (serverPublicKey != null
                    && serverSignature != null
                    && serverPublicKey.matches(BASE64_REGEX)
                    && serverSignature.matches(BASE64_REGEX)
                    && verifyData(serverPublicKey, serverSignature)) {
                val keyPair = generateDhPair()

                // Prepare public key
                val publicKeyBytes = publicKeyToBytes(keyPair.public as DHPublicKey)
                val encodedPublicKey = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)

                // Generate secret key
                val serverPublicKeyInstance = readDhPublicKey(serverPublicKey)
                val secretKey = generateDhSecretKey(keyPair.private, serverPublicKeyInstance)
                val secretKeyHash = getHash(secretKey)
                val secretHash = Base64.encodeToString(getHash(secretKeyHash.copyOf(16)), Base64.NO_WRAP)

                // Encrypt
                key = secretKeyHash

                // Send data to the server ...
                client.sendArray(arrayOf("upg", encodedPublicKey, secretHash))
            } else {
                handleAttack()
            }
        } else {
            handleAttack()
        }
    }

    private fun handleUpgrade(packet: JSONArray) {
        if (packet.length() >= 2 && packet.optInt(1) == 1) {
            // Ignore, key already was saved

            client.sendSecret()
        } else {
            handleAttack()
        }
    }

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

                val decryptedPayload = JSONArray(decryptAES(key!!, iv, payload))

                // Check length
                if (decryptedPayload.length() >= 3) {
                    val event = decryptedPayload.optString(0)
                    val message = decryptedPayload.optJSONObject(1)
                    val salt = decryptedPayload.optString(2)

                    if (event != null && message != null && salt != null && key != null) {
                        val hmacReaded = getHmac(key!!, event + message + salt)
                        val encodedHmac = Base64.encodeToString(hmacReaded, Base64.NO_WRAP)

                        if (encodedHmac == hmac) {
                            client.notifyCallbacks(event, message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun handleAttack() {
        client.connectionStateLiveData.postValue(ConnectState.ATTACKED)

        // Remove key & close connection
        handleEnd()
    }

    fun handleEnd() {
        key = null

        if (!client.socket.isClosed) {
            client.socket.close()
        }
    }
}