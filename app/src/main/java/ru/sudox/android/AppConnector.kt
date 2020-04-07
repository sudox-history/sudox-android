package ru.sudox.android

import android.net.ConnectivityManager
import io.reactivex.rxjava3.disposables.Disposable
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AppConnector : ConnectivityManager.NetworkCallback() {

    private var statusDisposable: Disposable? = null

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

    init {
        AppLoader.loaderComponent!!.inject(this)
    }

    fun start() {
        statusDisposable = sudoxApi!!
                .statusSubject
                .filter { it == SudoxApiStatus.NOT_CONNECTED }
                .delay(400, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (it == SudoxApiStatus.NOT_CONNECTED) {
                        sudoxApi!!.startConnection()
                    }
                }

        sudoxApi!!.startConnection()
    }

    fun destroy() {
        statusDisposable?.dispose()
        sudoxApi!!.endConnection()
    }
}