package com.sudox.android.di.module

import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.ProtocolHandshake
import dagger.Module
import dagger.Provides
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Module
class ProtocolModule {

    @Provides
    @Singleton
    fun provideProtocolClient(socket: Socket, handshake: ProtocolHandshake)
            = ProtocolClient(socket, handshake)

    @Provides
    @Singleton
    fun provideSocket() : Socket {
        val options = IO.Options()
                .apply {
                    reconnection = true
                    forceNew = true
                    transports = arrayOf("websocket")
                    path = "/"
                }

        // Create instance of socket
       return IO.socket("http://api.sudox.ru", options)
    }
}