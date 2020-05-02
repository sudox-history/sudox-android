package ru.sudox.android.auth.data.repositories

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.sudox.android.account.entries.AccountData
import ru.sudox.android.account.repository.AccountRepository
import ru.sudox.android.auth.data.daos.AuthSessionDAO
import ru.sudox.android.auth.data.entities.AuthSessionEntity
import ru.sudox.android.auth.data.entities.AuthSessionStage
import ru.sudox.android.auth.data.exceptions.AuthSessionTimeoutException
import ru.sudox.android.auth.data.exceptions.KeyExchangeAbandonedException
import ru.sudox.android.core.exceptions.InvalidFieldFormatException
import ru.sudox.android.countries.helpers.isPhoneNumberValid
import ru.sudox.api.auth.AUTH_CODE_INVALID_ERROR_CODE
import ru.sudox.api.auth.AUTH_DROPPED_ERROR_CODE
import ru.sudox.api.auth.AUTH_NOT_FOUND_ERROR_CODE
import ru.sudox.api.auth.AUTH_SESSION_LIFETIME
import ru.sudox.api.auth.AUTH_TYPE_INVALID_ERROR_CODE
import ru.sudox.api.auth.AuthService
import ru.sudox.api.auth.NAME_REGEX
import ru.sudox.api.auth.NICKNAME_REGEX
import ru.sudox.api.auth.entries.newauthverify.NewAuthVerifyDTO
import ru.sudox.api.common.SESSION_NOT_FOUND_ERROR_CODE
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.SudoxApiStatus
import ru.sudox.api.common.exceptions.ApiException
import ru.sudox.api.common.helpers.toHexByteArray
import ru.sudox.api.system.SystemService
import ru.sudox.cryptography.BLAKE2b
import ru.sudox.cryptography.Random
import ru.sudox.cryptography.X25519
import ru.sudox.cryptography.XChaCha20Poly1305
import ru.sudox.cryptography.entries.KeyPair
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        private val sudoxApi: SudoxApi,
        private val authService: AuthService,
        private val systemService: SystemService,
        private val phoneNumberUtil: PhoneNumberUtil,
        private val accountRepository: AccountRepository,
        private val sessionDao: AuthSessionDAO
) {

    private var keyPair: KeyPair? = null
    private var exchangeData: NewAuthVerifyDTO? = null

    var currentSession: AuthSessionEntity? = null
        private set

    var timerObservable: Observable<Long>? = null
        private set

    val authSessionErrorsSubject = PublishSubject.create<Throwable>()

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
                            timerObservable = Observable
                                    .timer(it.creationTime + AUTH_SESSION_LIFETIME - dto.time, TimeUnit.MILLISECONDS)
                                    .doOnComplete { destroySession(AuthSessionTimeoutException()) }
                                    .share()
                        }.flatMap { entity ->
                            if (currentSession!!.userExists) {
                                keyPair = X25519.generateKeyPair()

                                authService
                                        .verify(currentSession!!.authId, keyPair!!.publicKey)
                                        .doOnError { destroySession(it) }
                                        .map { entity }
                            } else {
                                Observable.just(entity)
                            }
                        }
            }
        } else {
            Observable.error(InvalidFieldFormatException(hashSetOf(0)))
        }
    }

    /**
     * Подтверждает сессию авторизации кодом, введенным пользователем.
     * Также вызывается обмен ключей с другим телефоном если пользователь зарегистрирован.
     *
     * @param code Код подтверждения
     * @throws InvalidFieldFormatException если код введен в неправильном формате.
     * @return Observable с состоянием регистрации пользователя
     */
    fun checkCode(code: Int): Observable<Boolean> {
        return if (code in 10000..99999) {
            authService
                    .checkCode(currentSession!!.authId, code)
                    .doOnError {
                        if (it is ApiException && (it.code == AUTH_DROPPED_ERROR_CODE || it.code == AUTH_NOT_FOUND_ERROR_CODE)) {
                            destroySession(it)
                        }
                    }.doOnNext {
                        sessionDao.update(currentSession!!.apply {
                            stage = AuthSessionStage.CODE_CHECKED
                        })
                    }.map { currentSession!!.userExists }
        } else {
            Observable.error(InvalidFieldFormatException(hashSetOf(0)))
        }
    }

    /**
     * Регистрирует пользователя.
     *
     * @param name Имя пользователя
     * @param nickname Никнейм пользователя
     * @throws InvalidFieldFormatException если одно из значений предоставлено в неверном формате (0 - имя, 1 - никнейм)
     * @return Observable с ответом от сервера
     */
    fun signUp(name: String, nickname: String): Observable<Unit> {
        val invalidFields = HashSet<Int>(2)

        if (!NAME_REGEX.matches(name)) {
            invalidFields.add(0)
        }

        if (!NICKNAME_REGEX.matches(nickname)) {
            invalidFields.add(1)
        }

        if (invalidFields.isNotEmpty()) {
            return Observable.error(InvalidFieldFormatException(invalidFields))
        }

        val accountKey = Random.generate(XChaCha20Poly1305.KEY_LENGTH)
        val accountKeyHash = BLAKE2b.hash(accountKey)

        return authService
                .signUp(currentSession!!.authId, name, nickname, accountKeyHash)
                .observeOn(Schedulers.computation())
                .doOnNext {
                    accountRepository.addAccount(AccountData(it.userId, currentSession!!.phoneNumber, it.userSecret, accountKey))
                    destroySession(null)
                }.doOnError {
                    if (it is ApiException && (it.code == SESSION_NOT_FOUND_ERROR_CODE ||
                                    it.code == AUTH_TYPE_INVALID_ERROR_CODE ||
                                    it.code == AUTH_CODE_INVALID_ERROR_CODE)) {

                        destroySession(it)
                    }
                }.map { Unit }
    }

    /**
     * Слушает получение запросов новых авторизаций.
     *
     * @return Observable с ответом от сервера
     */
    fun listenNewAuthSession(): Observable<Unit> {
        return authService
                .listenNewAuthSession()
                .doOnNext { exchangeData = it }
                .map { Unit }
    }

    /**
     * Отправляет ответ на запрос авторизации
     *
     * @param accept Разрешить авторизацию?
     * @return Observable с ответом от сервера
     */
    fun sendAuthResponse(accept: Boolean): Observable<Unit> {
        return if (accept) {
            keyPair = X25519.generateKeyPair()

            val nonce = Random.generate(XChaCha20Poly1305.NONCE_LENGTH)
            val cipherText = accountRepository.getAccountData()!!.key + nonce
            val encKeyPair = X25519.exchange(keyPair!!.publicKey, keyPair!!.secretKey, exchangeData!!.publicKey.toHexByteArray(), false)
            val keyForSending = XChaCha20Poly1305.encryptData(encKeyPair.sendKey, nonce, cipherText)

            authService.respondVerify(accept, keyPair!!.publicKey, exchangeData!!.authId, keyForSending)
        } else {
            authService.respondVerify(false, null, exchangeData!!.authId, null)
        }.doOnComplete {
            exchangeData = null
            keyPair = null
        }
    }

    /**
     * Слушает ответ от другого устройства.
     * При получении одобрения, производит сохранение аккаунта и перепрос на авторизацию.
     *
     * @return Observable с ответом от сервера
     */
    fun listenRespondAuthVerify(): Observable<Unit> {
        return authService
                .listenRespondAuthVerify()
                .observeOn(Schedulers.computation())
                .doOnError { destroySession(it) }
                .doOnNext {
                    sessionDao.update(currentSession!!.apply {
                        stage = AuthSessionStage.VERIFIED
                    })
                }.flatMap { dto ->
                    if (dto.accept) {
                        val pubKey = dto.publicKey!!.toHexByteArray()
                        val sessionKey = X25519.exchange(keyPair!!.publicKey, keyPair!!.secretKey, pubKey, true).receiveKey
                        val userKeyEnc = dto.userKeyEnc!!.toHexByteArray()
                        val nonce = userKeyEnc.copyOfRange(userKeyEnc.lastIndex - XChaCha20Poly1305.NONCE_LENGTH, userKeyEnc.lastIndex)
                        val key = XChaCha20Poly1305.decryptData(sessionKey, nonce, userKeyEnc.copyOf(XChaCha20Poly1305.KEY_LENGTH))

                        accountRepository.addAccount(AccountData(null, currentSession!!.phoneNumber, null, key))

                        authService
                                .signIn(currentSession!!.authId, BLAKE2b.hash(key))
                                .retryWhen { sudoxApi.statusSubject.filter { it == SudoxApiStatus.CONNECTED } }
                                .doOnNext {
                                    val data = AccountData(it.userId, currentSession!!.phoneNumber, it.userSecret, key)

                                    if (accountRepository.getAccountData() != null) {
                                        accountRepository.updateAccountData(data)
                                    } else {
                                        accountRepository.addAccount(data)
                                    }

                                    destroySession(null)
                                }
                    } else {
                        destroySession(KeyExchangeAbandonedException())
                        Observable.empty()
                    }
                }.map { Unit }
    }

    private fun destroySession(throwable: Throwable?) {
        sessionDao.delete(currentSession!!)
        timerObservable = null
        currentSession = null
        keyPair = null

        if (throwable != null) {
            authSessionErrorsSubject.onNext(throwable)
        }
    }

    private fun createSession(phone: String, time: Long): Observable<AuthSessionEntity> {
        return authService
                .create(phone)
                .map { AuthSessionEntity(phone, it.userExists, time, AuthSessionStage.PHONE_CHECKED, true, it.authId) }
                .observeOn(Schedulers.computation())
                .doOnNext { sessionDao.insert(it) }
    }

    private fun restoreSession(it: AuthSessionEntity, time: Long): Observable<AuthSessionEntity> {
        return if (it.creationTime + AUTH_SESSION_LIFETIME - time > 0 && !it.userExists) {
            it.isSelected = true
            sessionDao.update(it)

            Observable.just(it)
        } else {
            sessionDao.delete(it)
            Observable.empty()
        }
    }
}
