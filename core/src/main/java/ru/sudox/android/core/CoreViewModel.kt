package ru.sudox.android.core

import androidx.lifecycle.ViewModel
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class CoreViewModel : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    @Suppress("UnstableApiUsage")
    fun <T> doRequest(observable: Observable<T>): Observable<T> {
        return observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .materialize()
                .delay(400, TimeUnit.MILLISECONDS)
                .dematerialize {
                    if (it.isOnComplete) {
                        Notification.createOnComplete()
                    } else if (it.isOnError) {
                        Notification.createOnError(it.error!!)
                    } else {
                        Notification.createOnNext(it.value!!)
                    }
                }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}