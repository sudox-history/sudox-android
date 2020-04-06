package ru.sudox.android

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.core.net.ConnectivityManagerCompat
import io.reactivex.rxjava3.disposables.Disposable
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import javax.inject.Inject
import javax.inject.Named

class AppConnector {

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
        statusDisposable = sudoxApi!!.statusSubject.subscribe {
            if (it == SudoxApiStatus.DISCONNECTED) {

            }
        }
    }

    fun start() {
        sudoxApi!!.startConnection()
    }

    fun destroy() {
        statusDisposable?.dispose()
        sudoxApi!!.endConnection()
    }

    private fun canConnectToServer() {
        
    }
}