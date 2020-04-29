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
        private val authSessionDAO: AuthSessionDAO,
        private val phoneNumberUtil: PhoneNumberUtil
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
    fun createSession(phone: String): Observable<AuthSessionEntity> {
        // TODO: Переместить в CountriesRepository
        if (!phoneNumberUtil.isPhoneNumberValid(phone)) {
            return Observable.error(InvalidFieldFormatException(hashSetOf(0)))
        }

        return systemService.getTime().flatMap {
            val session = authSessionDAO.get(phone).firstOrNull()

            if (session != null) {
                val remainingTime = session.creationTime + AUTH_SESSION_LIFETIME - it.time

                if (remainingTime > 0) {
                    timerObservable = Observable.timer(remainingTime, TimeUnit.MILLISECONDS)

                    session.isActive = true
                    authSessionDAO.update(session)

                    Observable.just(session)
                } else {
                    authSessionDAO.delete(session)
                }
            }

            authService.create(phone).map { dto ->
                timerObservable = Observable.timer(AUTH_SESSION_LIFETIME, TimeUnit.MILLISECONDS)

                val entity = AuthSessionEntity(phone, dto.userExists, it.time, AuthSessionStage.PHONE_ENTERED, true, dto.authId)
                authSessionDAO.insert(entity)
                entity
            }
        }.subscribeOn(Schedulers.computation())
    }
}