package ru.sudox.android.database.helpers

import io.objectbox.Box

fun <T> observableCreate(box: Box<T>, data: T): io.reactivex.rxjava3.core.Observable<T> = io.reactivex.rxjava3.core.Observable.create<T> {
    try {
        it.onNext(box[box.put(data)])
    } catch (e: Exception) {
        it.onError(e)
    }

    it.onComplete()
}