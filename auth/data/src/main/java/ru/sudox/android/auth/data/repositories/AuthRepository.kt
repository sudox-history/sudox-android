package ru.sudox.android.auth.data.repositories

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import ru.sudox.android.auth.data.daos.AuthSessionDAO
import ru.sudox.android.auth.data.entities.AuthSessionEntity
import ru.sudox.api.auth.AuthService
import ru.sudox.api.common.exceptions.ApiException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        val authService: AuthService,
        val authSessionDAO: AuthSessionDAO
) {

    fun createSessionOrRestore(phoneNumber: String): Observable<AuthSessionEntity> {
        return authSessionDAO
                .get(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .toObservable()
                .flatMap { list ->
                    if (list.isEmpty()) {
                        createSession(phoneNumber)
                    } else {
                        restoreSession(list.first())
                    }
                }
    }

    private fun createSession(phoneNumber: String): Observable<AuthSessionEntity> {
        return authService
                .createSession(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap {
                    val entity = AuthSessionEntity(phoneNumber, it.userExists, it.authToken)

                    authSessionDAO
                            .insert(entity)
                            .toObservable()
                            .map { entity }
                }
    }

    private fun restoreSession(entity: AuthSessionEntity): Observable<AuthSessionEntity> {
        return authService
                .restoreSession(entity.token)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { entity }
                .onErrorResumeNext(Function<Throwable, ObservableSource<out AuthSessionEntity>> {
                    if (it is ApiException) {
                        authSessionDAO
                                .delete(entity)
                                .toObservable()
                                .flatMap { createSession(entity.phoneNumber) }
                    } else {
                        Observable.error(it)
                    }
                })
    }

    fun checkCode(code: Int): Observable<Nothing>{
        return authService
                .checkCode(code)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnError {
                    Observable.error<Throwable>(it)
                }
    }
}