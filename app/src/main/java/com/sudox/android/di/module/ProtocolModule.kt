package com.sudox.android.di.module

import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.ProtocolConnectionStabilizer
import com.sudox.protocol.ProtocolHandshake
import com.sudox.protocol.ProtocolKeystore
import dagger.Module
import dagger.Provides
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Module
class ProtocolModule {

    @Provides
    @Singleton
    fun provideProtocolClient(
            socket: Socket) = ProtocolClient(socket)

    @Provides
    fun provideProtocolHandshake(protocolClient: ProtocolClient,
                                 keystore: ProtocolKeystore) : ProtocolHandshake {
        return ProtocolHandshake(protocolClient, keystore)
    }

    @Provides
    fun provideProtocolKeystore() : ProtocolKeystore = ProtocolKeystore()

    @Provides
    @Singleton
    fun provideProtocolConnectionStabilizer(handshake: ProtocolHandshake,
                                            socket: Socket) : ProtocolConnectionStabilizer {
        return ProtocolConnectionStabilizer(handshake, socket)
    }

    @Provides
    @Singleton
    fun provideSocket() : Socket {
        val options = IO.Options()
                .apply {
                    reconnection = true
                    secure = true
                    path = "/"
                }

        // Create instance of socket
       return IO.socket("http://api.sudox.ru", options)
    }
}