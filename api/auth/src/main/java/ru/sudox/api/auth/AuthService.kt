package ru.sudox.api.auth

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Observable
import ru.sudox.api.auth.entries.checkcode.AuthCheckCodeRequestDTO
import ru.sudox.api.auth.entries.create.AuthCreateRequestDTO
import ru.sudox.api.auth.entries.create.AuthCreateResponseDTO
import ru.sudox.api.auth.entries.newauthverify.NewAuthVerifyDTO
import ru.sudox.api.auth.entries.respondauthverify.AuthRespondAuthVerifyDTO
import ru.sudox.api.auth.entries.respondverify.AuthRespondVerifyRequestDTO
import ru.sudox.api.auth.entries.signin.AuthSignInRequestDTO
import ru.sudox.api.auth.entries.signin.AuthSignInResponseDTO
import ru.sudox.api.auth.entries.signup.AuthSignUpRequestDTO
import ru.sudox.api.auth.entries.signup.AuthSignUpResponseDTO
import ru.sudox.api.auth.entries.verify.AuthVerifyRequestDTO
import ru.sudox.api.auth.helpers.isPhoneNumberValid
import ru.sudox.api.common.SudoxApi
import ru.sudox.api.common.exceptions.ApiRegexException
import ru.sudox.api.common.helpers.toHexString

class AuthService(
        private val sudoxApi: SudoxApi,
        private val phoneNumberUtil: PhoneNumberUtil
) {

    /**
     * Создает сессию авторизации.
     *
     * @param userPhone Номер телефона
     * @throws ApiRegexException Если номер телефона не прошел проверку.
     * @return Observable на который прилетит ответ от сервера.
     */
    fun create(userPhone: String): Observable<AuthCreateResponseDTO> {
        if (!phoneNumberUtil.isPhoneNumberValid(userPhone)) {
            return Observable.error(ApiRegexException(0))
        }

        return sudoxApi.sendRequest("auth.create", AuthCreateRequestDTO(userPhone), AuthCreateResponseDTO::class.java)
    }

    /**
     * Подтверждает сессию кодом подтверждения.
     *
     * @param authId ID сессии авторизации
     * @param authCode Код подтверждения
     * @throws ApiRegexException Если какое-то поле не прошло проверку (1 - код)
     * @return Observable на который прилетит ответ от сервера.
     */
    fun checkCode(authId: String, authCode: Int): Observable<Unit> {
        if (authCode !in 10000 .. 99999) {
            return Observable.error(ApiRegexException(1))
        }

        return sudoxApi.sendRequest("auth.checkCode", AuthCheckCodeRequestDTO(authId, authCode), Unit::class.java)
    }

    /**
     * Отправляет подтверждения авторизации на другой авторизированный телефон пользователя
     *
     * @param authId ID сессии авторизации
     * @param publicKey Публичный ключ
     * @return Observable на который прилетит ответ от сервера.
     */
    fun verify(authId: String, publicKey: ByteArray): Observable<Unit> {
        return sudoxApi.sendRequest("auth.verify", AuthVerifyRequestDTO(authId, publicKey.toHexString()), Unit::class.java)
    }

    /**
     * Регистрирует пользователя
     *
     * @param authId ID сессии авторизации
     * @param userName Имя пользователя
     * @param userNickname Никнейм пользователя
     * @param userKeyHash Хэш ключа пользователя (по BLAKE2b)
     * @throws ApiRegexException Если какое-то поле не прошло проверку (1 - имя, 2 - никнейм)
     * @return Observable на который прилетит ответ от сервера.
     */
    fun signUp(authId: String, userName: String, userNickname: String, userKeyHash: ByteArray): Observable<AuthSignUpResponseDTO> {
        val userNameInvalid = !USERNAME_REGEX.matches(userName)
        val userNicknameInvalid = !USERNICKNAME_REGEX.matches(userNickname)

        if (userNameInvalid && userNicknameInvalid) {
            return Observable.error(ApiRegexException(1, 2))
        } else if (userNameInvalid) {
            return Observable.error(ApiRegexException(1))
        } else if (userNicknameInvalid) {
            return Observable.error(ApiRegexException(2))
        }

        return sudoxApi.sendRequest("auth.signUp", AuthSignUpRequestDTO(authId, userName, userNickname, userKeyHash.toHexString()), AuthSignUpResponseDTO::class.java)
    }

    /**
     * Авторизует пользователя по хэшу ключа.
     *
     * @param authId ID сессии авторизации
     * @param userKeyHash Хэш ключа пользователя (по BLAKE2b)
     * @return Observable на который прилетит ответ от сервера.
     */
    fun signIn(authId: String, userKeyHash: ByteArray): Observable<AuthSignInResponseDTO> {
        return sudoxApi.sendRequest("auth.signIn", AuthSignInRequestDTO(authId, userKeyHash.toHexString()), AuthSignInResponseDTO::class.java)
    }

    /**
     * Отвечает на запрос авторизации
     *
     * @param accept Одобрен ли запрос?
     * @param publicKey Публичный ключ
     * @param authId ID сессии авторизации
     * @param userKeyEnc Зашифрованный ключ пользователя
     * @return Observable на который прилетит ответ от сервера.
     */
    fun respondVerify(accept: Boolean, publicKey: ByteArray, authId: String, userKeyEnc: ByteArray): Observable<Unit> {
        return sudoxApi.sendRequest("auth.respondVerify", AuthRespondVerifyRequestDTO(accept, publicKey.toHexString(), authId, userKeyEnc.toHexString()), Unit::class.java)
    }

    /**
     * Подписывает на получения эвента о запросе получения ключа пользователя
     */
    fun listenNewAuthSession(): Observable<NewAuthVerifyDTO> {
        return sudoxApi.listenUpdate("updates.newAuthVerify", NewAuthVerifyDTO::class.java)
    }

    /**
     * Подписывает на получение эвента о статусе запроса на получения ключа пользователя
     */
    fun listenRespondAuthVerify(): Observable<AuthRespondAuthVerifyDTO> {
        return sudoxApi.listenUpdate("updates.respondAuthVerify", AuthRespondAuthVerifyDTO::class.java)
    }
}