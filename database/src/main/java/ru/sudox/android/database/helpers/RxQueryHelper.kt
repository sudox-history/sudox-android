package ru.sudox.android.database.helpers

import io.objectbox.Box
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> observableCreate(box: Box<T>, data: T): io.reactivex.rxjava3.core.Observable<T> = io.reactivex.rxjava3.core.Observable.create<T> {
    try {
        it.onNext(box[box.put(data)])
    } catch (e: Exception) {
        it.onError(e)
    }

    it.onComplete()
}.subscribeOn(Schedulers.computation())