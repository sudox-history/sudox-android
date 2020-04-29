package ru.sudox.android.auth.data.repositories

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.sudox.android.auth.data.daos.AuthSessionDAO
import ru.sudox.android.auth.data.entities.AuthSessionEntity
import ru.sudox.android.auth.data.entities.AuthSessionStage
import ru.sudox.android.core.exceptions.InvalidFieldFormatException
import ru.sudox.api.auth.AUTH_DROPPED_ERROR_CODE
import ru.sudox.api.auth.AUTH_NOT_FOUND_ERROR_CODE
import ru.sudox.api.auth.AUTH_SESSION_LIFETIME
import ru.sudox.api.auth.AuthService
import ru.sudox.api.auth.helpers.isPhoneNumberValid
import ru.sudox.api.common.exceptions.ApiException
import ru.sudox.api.system.SystemService
import ru.sudox.cryptography.Random
import ru.sudox.cryptography.XChaCha20Poly1305
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
        private set

    var currentSession: AuthSessionEntity? = null
        private set

    var currentPublicKey: ByteArray? = null
        private set

    var currentPrivateKey: ByteArray? = null
        private set

    val sessionDestroyedSubject = PublishSubject.create<Int>()

    /**
     * Создает сессию авторизации.
     *
     * Если в БД будет найдена живая сессия с указанным номером,
     * то она будет восстановлена.
     *
     * @param phone Номер телефона, под которым нужно провести авторизацию
     * @throws InvalidFieldFormatException если телефон введен в неправильном формате.
     * @return Observable с сущностью <u>в виде гномика</u> сессии авторизации
     */
    fun createOrRestoreSession(phone: String): Observable<AuthSessionEntity> {
        return if (phoneNumberUtil.isPhoneNumberValid(phone)) {
            systemService.getTime().flatMap { dto ->
                sessionDao
                        .get(phone)
                        .subscribeOn(Schedulers.computation())
                        .toObservable()
                        .flatMap { restoreSession(it, dto.time) }
                        .switchIfEmpty(createSession(phone, dto.time))
                        .doOnNext {
                            currentSession = it
                            timerObservable = Observable.timer(it.creationTime + AUTH_SESSION_LIFETIME - dto.time, TimeUnit.MILLISECONDS)
                        }
            }
        } else {
            Observable.error(InvalidFieldFormatException(hashSetOf(0)))
        }
    }

    fun checkCode(code: Int): Observable<Boolean> {
        return if (code in 10000..99999) {
            authService
                    .checkCode(currentSession!!.authId, code)
                    .doOnError {
                        if (it is ApiException && (it.code == AUTH_DROPPED_ERROR_CODE || it.code == AUTH_NOT_FOUND_ERROR_CODE)) {
                            destroySession(it.code)
                        }
                    }.doOnNext {
                        sessionDao.update(currentSession!!.apply {
                            stage = AuthSessionStage.CODE_CHECKED
                        })
                    }.flatMap {
                        if (currentSession!!.userExists) {
                            currentPublicKey = Random.generate(XChaCha20Poly1305.KEY_LENGTH)
                            currentPrivateKey = Random.generate(XChaCha20Poly1305.KEY_LENGTH)

                            authService
                                    .verify(currentSession!!.authId, currentPublicKey!!)
                                    .doOnError { destroySession(AUTH_NOT_FOUND_ERROR_CODE) }
                        } else {
                            Observable.empty()
                        }
                    }.map { currentSession!!.userExists }
        } else {
            Observable.error(InvalidFieldFormatException(hashSetOf(0)))
        }
    }

    private fun destroySession(code: Int) {
        sessionDao.delete(currentSession!!)
        timerObservable = null
        currentSession = null

        sessionDestroyedSubject.onNext(code)
    }

    private fun createSession(phone: String, time: Long): Observable<AuthSessionEntity> {
        return authService
                .create(phone)
                .map { AuthSessionEntity(phone, it.userExists, time, AuthSessionStage.PHONE_CHECKED, true, it.authId) }
                .observeOn(Schedulers.computation())
                .doOnNext { sessionDao.insert(it) }
    }

    private fun restoreSession(it: AuthSessionEntity, time: Long): Observable<AuthSessionEntity> {
        return if (it.creationTime + AUTH_SESSION_LIFETIME - time > 0) {
            it.isSelected = true
            sessionDao.update(it)

            Observable.just(it)
        } else {
            sessionDao.delete(it)
            Observable.empty()
        }
    }
}
