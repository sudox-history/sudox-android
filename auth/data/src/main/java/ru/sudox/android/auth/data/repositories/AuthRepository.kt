package ru.sudox.android.auth.data.repositories

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.sudox.android.auth.data.daos.AuthSessionDAO
import ru.sudox.android.auth.data.entities.AuthSessionEntity
import ru.sudox.android.auth.data.entities.AuthSessionStage
import ru.sudox.android.core.exceptions.InvalidFieldFormatException
import ru.sudox.api.auth.AUTH_SESSION_LIFETIME
import ru.sudox.api.auth.AuthService
import ru.sudox.api.auth.helpers.isPhoneNumberValid
import ru.sudox.api.system.SystemService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        private val authService: AuthService,
        private val systemService: SystemService,
        private val phoneNumberUtil: PhoneNumberUtil,
        private val sessionDao: AuthSessionDAO
) {

    var timerObservable: Observable<Long>? = null

    /**
     * Создает сессию авторизации.
     *
     * Если в БД будет найдена живая сессия с указанным номером,
     * то она будет восстановлена.
     *
     * @param phone Номер телефона, под которым нужно провести авторизацию
     * @return Observable с сущностью <u>в виде гномика</u> сессии авторизации
     */
    fun createOrRestoreSession(phone: String): Observable<AuthSessionEntity> {
        return if (phoneNumberUtil.isPhoneNumberValid(phone)) {
            systemService.getTime().flatMap { dto ->
                sessionDao
                        .get(phone)
                        .subscribeOn(Schedulers.computation())
                        .toObservable()
                        .flatMap { restoreSession(it,  dto.time) }
                        .switchIfEmpty(createSession(phone,  dto.time))
            }
        } else {
            Observable.error(InvalidFieldFormatException(hashSetOf(0)))
        }
    }

    private fun createSession(phone: String, time: Long): Observable<AuthSessionEntity> {
        return authService
                .create(phone)
                .map { AuthSessionEntity(phone, it.userExists, time, AuthSessionStage.PHONE_ENTERED, true, it.authId) }
                .observeOn(Schedulers.computation())
                .doOnNext { sessionDao.insert(it) }
    }

    private fun restoreSession(it: AuthSessionEntity, time: Long): Observable<AuthSessionEntity> {
        val remainingTime = it.creationTime + AUTH_SESSION_LIFETIME - time

        return if (remainingTime > 0) {
            timerObservable = Observable.timer(remainingTime, TimeUnit.MILLISECONDS)

            it.isSelected = true
            sessionDao.update(it)

            Observable.just(it)
        } else {
            sessionDao.delete(it)
            Observable.empty()
        }
    }
}
