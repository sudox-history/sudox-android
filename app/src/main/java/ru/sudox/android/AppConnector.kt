package ru.sudox.android

import android.net.ConnectivityManager
import io.reactivex.disposables.Disposable
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Коннектор API.
 *
 * Отвечает за:
 * 1) Установку соединения с сервером;
 * 2) Восстановление соединения при его падении.
 */
class AppConnector : ConnectivityManager.NetworkCallback() {

    private var statusDisposable: Disposable? = null

    @Inject
    @JvmField
    var sudoxApi: SudoxApi? = null

    init {
        AppLoader.loaderComponent!!.inject(this)
    }

    /**
     * Запускает установку соединения с сервером и его восстановитель.
     */
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

    /**
     * Останавливает соединение с сервером и его восстановитель.
     */
    fun destroy() {
        statusDisposable?.dispose()
        sudoxApi!!.endConnection()
    }
}