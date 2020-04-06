package ru.sudox.android.managers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import ru.sudox.api.common.SudoxApi

class AppNetworkManager(
        val sudoxApi: SudoxApi
) : ConnectivityManager.NetworkCallback() {

    private var connectivityManager: ConnectivityManager? = null

    override fun onLost(network: Network) {
        sudoxApi.endConnection()
    }

    override fun onAvailable(network: Network) {
        sudoxApi.startConnection()
    }

    fun register(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val request = NetworkRequest
                .Builder()
                .build()

        connectivityManager!!.registerNetworkCallback(request, this)
    }

    fun unregister() {
        connectivityManager!!.unregisterNetworkCallback(this)
    }
}