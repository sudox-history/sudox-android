package com.sudox.protocol

import com.sudox.android.ApplicationLoader
import com.sudox.protocol.exception.HandshakeException
import com.sudox.protocol.helper.encryptRSA
import com.sudox.protocol.helper.getHashString
import com.sudox.protocol.helper.randomBase64String
import com.sudox.protocol.model.SymmetricKey
import com.sudox.protocol.model.dto.HandshakeRandomDTO
import com.sudox.protocol.model.dto.HandshakeSignatureDTO
import com.sudox.protocol.model.dto.HandshakeUpgradeFromServerDTO
import com.sudox.protocol.model.dto.HandshakeUpgradeToServerDTO
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import javax.inject.Inject

class ProtocolHandshake @Inject constructor(private var protocolKeystore: ProtocolKeystore) {

    @Inject
    lateinit var protocolClient: ProtocolClient

    init {
        ApplicationLoader.component.inject(this)
    }

    // Disposables
    private lateinit var disposables: CompositeDisposable

    fun execute(): Single<SymmetricKey> = Single.create<SymmetricKey> {
        disposables = CompositeDisposable()

        // Get random hex string
        val random = randomBase64String(32)

        // Set listener
        val disposable = protocolClient.listenMessageHandshake("verify", HandshakeSignatureDTO::class)
                .subscribe(ProtocolHandshakeObserver(protocolClient, it, protocolKeystore, random, disposables))

        // Add disposable to composite disposable
        disposables.add(disposable)

        // Create message with random hex-string
        val handshakeRandomDTO = HandshakeRandomDTO().apply {
            this.random = random
        }

        // Send message to the server and start handshake
        protocolClient.sendHandshakeMessage("verify", handshakeRandomDTO)
    }

    fun recycle() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    class ProtocolHandshakeObserver(private var protocolClient: ProtocolClient,
                                    private var handshakeEmitter: SingleEmitter<SymmetricKey>,
                                    private var protocolKeystore: ProtocolKeystore,
                                    private var random: String,
                                    private var disposables: CompositeDisposable) : (HandshakeSignatureDTO) -> (Unit) {

        override fun invoke(handshakeSignatureDTO: HandshakeSignatureDTO) {
            if (handshakeSignatureDTO.signature == null) {
                handshakeEmitter.onError(HandshakeException())
                disposables.dispose()

                return
            }

            val publicKey = protocolKeystore.findKey(random, handshakeSignatureDTO.signature!!)

            // If signature valid, then key wasn't equals the null
            if (publicKey != null) {
                val symmetricKey = SymmetricKey()

                // Initialize the symmetric key
                symmetricKey.generate()

                // Handshake payload json object
                val handshakeJsonObject = JSONObject()

                // Build the handshake payload
                val encryptedPayload = with(handshakeJsonObject) {
                    put("key", symmetricKey.key)
                    put("random", random)

                    encryptRSA(publicKey, toString())
                }

                // Get the hash
                val hash = getHashString(symmetricKey.key + random)

                // Build the handshake upgrade message
                val handshakeUpgradeDTO = HandshakeUpgradeToServerDTO().apply {
                    this.payload = encryptedPayload
                    this.hash = hash
                }

                // Set the listener for upgrade event
                val disposable = protocolClient.listenMessageHandshake("upgrade", HandshakeUpgradeFromServerDTO::class)
                        .subscribe(ProtocolUpgradeObserver(symmetricKey, handshakeEmitter, disposables))

                // Add disposable to the list
                disposables.add(disposable)

                // Send upgrade message
                protocolClient.sendHandshakeMessage("upgrade", handshakeUpgradeDTO)
            } else {
                handshakeEmitter.onError(HandshakeException())

                // Clear all
                disposables.dispose()
            }
        }
    }

    class ProtocolUpgradeObserver(private val symmetricKey: SymmetricKey,
                                  private val handshakeEmitter: SingleEmitter<SymmetricKey>,
                                  private val disposables: CompositeDisposable) : (HandshakeUpgradeFromServerDTO) -> (Unit) {

        override fun invoke(handshakeUpgradeFromServerDTO: HandshakeUpgradeFromServerDTO) {
            if (handshakeUpgradeFromServerDTO.code == 1) {
                handshakeEmitter.onSuccess(symmetricKey)
            } else {
                handshakeEmitter.onError(HandshakeException())
            }

            // Clear all
            disposables.dispose()
        }
    }
}

