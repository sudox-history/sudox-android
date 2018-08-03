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
    fun provideProtocolClient(socket: Socket, handshake: ProtocolHandshake, stabilizer: ProtocolConnectionStabilizer)
            = ProtocolClient(socket, handshake, stabilizer)

    @Provides
    @Singleton
    fun provideProtocolHandshake(keystore: ProtocolKeystore)
            = ProtocolHandshake(keystore)

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