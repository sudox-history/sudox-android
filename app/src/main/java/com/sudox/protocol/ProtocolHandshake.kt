package com.sudox.protocol

import com.sudox.protocol.helper.encryptRSA
import com.sudox.protocol.helper.getHashString
import com.sudox.protocol.helper.randomBase64String
import com.sudox.protocol.model.SymmetricKey
import com.sudox.protocol.model.dto.HandshakeRandomDTO
import com.sudox.protocol.model.dto.HandshakeSignatureDTO
import com.sudox.protocol.model.dto.HandshakeUpgradeFromServerDTO
import com.sudox.protocol.model.dto.HandshakeUpgradeToServerDTO
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolHandshake @Inject constructor(private var protocolKeystore: ProtocolKeystore) {

    private fun validate(successCallback: (SymmetricKey) -> Unit,
                         errorCallback: (Unit) -> (Unit),
                         protocolClient: ProtocolClient,
                         handshakeSignatureDTO: HandshakeSignatureDTO,
                         random: String) {

        // For null-safety
        val signature = handshakeSignatureDTO.signature

        if (signature != null) {
            val publicKey = protocolKeystore.findKey(random, signature)

            if (publicKey != null) {
                val symmetricKey = SymmetricKey().apply {
                    generate()
                }

                // Build the handshake payload
                val payload = with(JSONObject()) {
                    put("key", symmetricKey.key)
                    put("random", random)
                    encryptRSA(publicKey, toString())
                }

                // Get the hash
                val hash = getHashString(symmetricKey.key + random)

                // Build the handshake upgrade message
                val handshakeUpgradeDTO = HandshakeUpgradeToServerDTO().apply {
                    this.payload = payload
                    this.hash = hash
                }

                // Check the result of handshake
                protocolClient.listenMessageHandshake("upgrade", HandshakeUpgradeFromServerDTO::class) {
                    if (it.code == 1) {
                        successCallback(symmetricKey)
                    } else {
                        errorCallback(Unit)
                    }
                }

                // Send upgrade message
                protocolClient.sendHandshakeMessage("upgrade", handshakeUpgradeDTO)
            } else errorCallback(Unit)
        } else errorCallback(Unit)
    }

    fun start(successCallback: (SymmetricKey) -> (Unit),
              errorCallback: (Unit) -> (Unit),
              protocolClient: ProtocolClient) {

        val random = randomBase64String(32)

        // Set listener
        protocolClient.listenMessageHandshake("verify", HandshakeSignatureDTO::class) {
            validate(successCallback, errorCallback, protocolClient, it, random)
        }

        // Create message with random hex-string
        val handshakeRandomDTO = HandshakeRandomDTO().apply {
            this.random = random
        }

        // Send message to the server and start handshake
        protocolClient.sendHandshakeMessage("verify", handshakeRandomDTO)
    }
}