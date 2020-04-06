package ru.sudox.android

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import io.reactivex.rxjava3.disposables.Disposable
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import javax.inject.Inject
import javax.inject.Named

class AppConnector : ConnectivityManager.NetworkCallback() {

    private var statusDisposable: Disposable? = null
    private var connectivityManager: ConnectivityManager? = null

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

    @Inject
    @JvmField
    @Named(APP_CONTEXT_NAME)
    var context: Context? = null

    init {
        AppLoader.loaderComponent!!.inject(this)

        connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    }

    override fun onLost(network: Network) {
        sudoxApi!!.endConnection()
    }

    override fun onAvailable(network: Network) {
        sudoxApi!!.startConnection()
    }

    fun start() {
        val request = NetworkRequest.Builder().build()

        connectivityManager!!.registerNetworkCallback(request, this)
        statusDisposable = sudoxApi!!.statusSubject.subscribe {
            if (it == SudoxApiStatus.DISCONNECTED && checkInternalAvailability()) {
                sudoxApi!!.startConnection()
            }
        }

        sudoxApi!!.startConnection()
    }

    fun destroy() {
        connectivityManager!!.unregisterNetworkCallback(this)
        statusDisposable?.dispose()
        sudoxApi!!.endConnection()
    }

    private fun checkInternalAvailability(): Boolean {
        return connectivityManager!!.allNetworks.indexOfFirst {
            connectivityManager!!.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        } != -1
    }
}