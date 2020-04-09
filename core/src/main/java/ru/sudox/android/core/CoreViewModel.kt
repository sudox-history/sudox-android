package ru.sudox.android.core

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

abstract class CoreViewModel : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    fun <T> doRequest(observable: Observable<T>): Observable<T> {
        return observable
                .materialize()
                .delay(400, TimeUnit.MILLISECONDS)
                .dematerialize {
                    if (it.isOnComplete) {
                        Notification.createOnComplete<T>()
                    } else if (it.isOnError) {
                        Notification.createOnError<T>(it.error)
                    } else {
                        Notification.createOnNext<T>(it.value)
                    }
                }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}