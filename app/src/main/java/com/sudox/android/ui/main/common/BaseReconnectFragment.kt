package com.sudox.android.ui.main.common

import com.sudox.android.ApplicationLoader
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Уведомляет каждого наследника о статусе соединения
 *
 * @author KerJen
 * **/
@Deprecated("")
abstract class BaseReconnectFragment : DaggerFragment() {

    @Inject
    lateinit var protocolClient: ProtocolClient
    internal var connectionStateSubscription: ReceiveChannel<ConnectionState>? = null

    abstract fun showConnectionStatus(isConnect: Boolean)

    init {
        ApplicationLoader.component.inject(this)
    }

    override fun onResume() {
        super.onResume()

        if (!protocolClient.isValid()) {
            showConnectionStatus(false)
        } else {
            showConnectionStatus(true)
        }

        listenForConnection()
    }

    private fun listenForConnection() = GlobalScope.launch(Dispatchers.IO) {
        connectionStateSubscription = protocolClient
                .connectionStateChannel
                .openSubscription()

        for (state in connectionStateSubscription!!) {
            val status: Boolean = if (state == ConnectionState.CONNECTION_CLOSED) {
                false
            } else state == ConnectionState.HANDSHAKE_SUCCEED

            GlobalScope.launch(Dispatchers.Main) { showConnectionStatus(status) }
        }
    }

    override fun onPause() {
        connectionStateSubscription?.cancel()

        // Super!
        super.onPause()
    }
}