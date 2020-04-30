package ru.sudox.android.core

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class CoreViewModel : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    @Suppress("UnstableApiUsage")
    fun <T> doRequest(observable: Observable<T>): Observable<T> {
        return observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}