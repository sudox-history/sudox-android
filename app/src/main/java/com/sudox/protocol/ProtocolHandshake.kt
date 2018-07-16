package com.sudox.protocol

import com.sudox.protocol.helper.randomHexString
import com.sudox.protocol.model.SymmetricKey
import com.sudox.protocol.model.dto.HandshakeRandomDTO
import com.sudox.protocol.model.dto.HandshakeSignatureDTO
import io.reactivex.Single

class ProtocolHandshake(private var protocolClient: ProtocolClient) {

    // Data for signature (Prevent MITM-attacks)
    private lateinit var signatureData: String

    fun execute() = Single.create<SymmetricKey> {
        protocolClient.listenMessageOnce("verify", HandshakeSignatureDTO::class)
                .subscribe(ProtocolHandshakeObserver())

        // Generate random hex string
        val random = randomHexString(64)

        //

        // Create message with random hex-string
        val handshakeRandomDTO = HandshakeRandomDTO(random = randomHexString(64))

        // Send message to the server and start handshake
        protocolClient.sendMessage("verify", handshakeRandomDTO, false)
    }

    class ProtocolHandshakeObserver : (HandshakeSignatureDTO) -> (Unit) {

        override fun invoke(handshakeSignatureDTO: HandshakeSignatureDTO) {

        }
    }
}