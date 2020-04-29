package ru.sudox.android.core

import android.util.Log
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
        var newObservable = observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .materialize()
                .delay(400, TimeUnit.MILLISECONDS)
                .dematerialize {
                    if (it.isOnComplete) {
                        Notification.createOnComplete<T>()
                    } else if (it.isOnError) {
                        Notification.createOnError<T>(it.error!!)
                    } else {
                        Notification.createOnNext<T>(it.value!!)
                    }
                }

        if (BuildConfig.DEBUG) {
            newObservable = newObservable.doOnError { Log.d("Sudox Core", "Error during request executing", it) }
        }

        return newObservable
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}