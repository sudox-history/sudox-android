package io.reactivex.rxjava3.internal.operators.observable

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.throttleLastIncludingErrors(period: Long, unit: TimeUnit): Observable<T> {
    return sampleIncludingErrors(period, unit)
}

fun <T> Observable<T>.sampleIncludingErrors(period: Long, unit: TimeUnit, scheduler: Scheduler = Schedulers.computation()): Observable<T> {
    return RxJavaPlugins.onAssembly(CustomObservableSampleTimed(this, period, unit, scheduler, false))
}