package com.sudox.android.ui.main.common

import android.arch.lifecycle.Observer
import com.sudox.android.ApplicationLoader
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Уведомляет каждого наследника о статусе соединения
 *
 * @author KerJen
 * **/
abstract class BaseReconnectFragment : DaggerFragment() {

    @Inject
    lateinit var protocolClient: ProtocolClient

    abstract fun showConnectionStatus(isConnect: Boolean)

    init {
        ApplicationLoader.component.inject(this)
    }

    fun listenForConnection() {
        protocolClient
                .connectionStateLiveData
                .observe(this, Observer {

                    val state: Boolean = if (it == ConnectionState.CONNECTION_CLOSED) {
                        false
                    } else it == ConnectionState.HANDSHAKE_SUCCEED

                    showConnectionStatus(state)
                })
    }

    override fun onResume() {
        super.onResume()

        if (!protocolClient.isValid()) {
            showConnectionStatus(false)
        } else {
            showConnectionStatus(true)
        }
    }
}