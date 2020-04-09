package ru.sudox.android

import android.net.ConnectivityManager
import io.reactivex.rxjava3.disposables.Disposable
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Коннектор API.
 *
 * Отвечает за:
 * 1) Установку соединения с сервером;
 * 2) Восстановление соединения при его падении.
 */
@Singleton
class AppConnector @Inject constructor(
        val sudoxApi: SudoxApi
) : ConnectivityManager.NetworkCallback() {

    private var statusDisposable: Disposable? = null

    /**
     * Запускает установку соединения с сервером и его восстановитель.
     */
    fun start() {
        statusDisposable = sudoxApi
                .statusSubject
                .filter { it == SudoxApiStatus.NOT_CONNECTED }
                .delay(400, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (it == SudoxApiStatus.NOT_CONNECTED) {
                        sudoxApi.startConnection()
                    }
                }

        sudoxApi.startConnection()
    }

    /**
     * Останавливает соединение с сервером и его восстановитель.
     */
    fun destroy() {
        statusDisposable?.dispose()
        sudoxApi.endConnection()
    }
}