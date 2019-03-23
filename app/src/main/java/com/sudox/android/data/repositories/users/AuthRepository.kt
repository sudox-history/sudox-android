package com.sudox.android.data.repositories.users

import com.sudox.android.common.helpers.*
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.auth.dto.*
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.data.models.common.Errors
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val accountRepository: AccountRepository,
                                         private val usersRepositoryProvider: Provider<UsersRepository>) {

    @JvmField
    var accountSessionStateChannel: ConflatedBroadcastChannel<Boolean> = ConflatedBroadcastChannel()
    @JvmField
    var authSessionChannel: ConflatedBroadcastChannel<AuthSession> = ConflatedBroadcastChannel()
    val usersRepository: UsersRepository = usersRepositoryProvider.get()
    var sessionInstalled: Boolean = false

    init {
        protocolClient.listenErrorCodes {
            if (it == Errors.UNAUTHORIZED) removeAccountSession()
        }

        // Start working ...
        listenConnectionState()
    }

    companion object {
        const val AUTH_NAME_REGEX_ERROR = 0
        const val AUTH_NICKNAME_REGEX_ERROR = 1
    }

    /**
     * Начинает прослушку событий протокола.
     *
     * При успешной установки соединения с сервером - проверяет наличие аккаунта, токена и если два
     * условия выполнены - импортирует токен.
     */
    fun listenConnectionState() = GlobalScope.launch(Dispatchers.IO) {
        for (state in protocolClient.connectionStateChannel.openSubscription()) {
            if (state == ConnectionState.HANDSHAKE_SUCCEED) {
                val account = accountRepository.getAccount()

                if (account != null) {
                    val token = accountRepository.getAccountToken(account)

                    // Т.к. к хранилищу AccountManager'а можно добраться имея Root-права,
                    // то мы должны учитывать, что злоумышленник может удалить токен и тем самым
                    // нарушить работу приложения. Для того, чтобы избежать этого, нам и нужна
                    // проверка на существование токена.
                    if (token != null) {
                        installAccountSession(token)
                    } else {
                        removeAccountSession()
                    }
                } else {
                    notifyAccountSessionInvalid()
                }
            } else if (state == ConnectionState.CONNECTION_CLOSED) {
                // Сессия не установлена. Блокируются запросы, связанные с сессией пользователя.
                // При попытке их выполнить будет выкинуто NetworkException.
                sessionInstalled = false
            }
        }
    }

    /**
     * Запрашивает отправку кода подтверждения на номер телефона, начинает сессию авторизации.
     *
     * @param phone - номер телефона
     */
    @Throws(RequestException::class)
    fun requestCode(phone: String) = GlobalScope.async(Dispatchers.IO) {
        if (!PHONE_REGEX.matches(phone)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        try {
            val authCodeDTO = protocolClient.makeRequest<AuthCodeDTO>("auth.sendCode", AuthCodeDTO().apply {
                this.phone = phone
            }).await()

            if (authCodeDTO.isSuccess()) {
                authSessionChannel.offer(AuthSession(phone, authCodeDTO.hash, authCodeDTO.status))
            } else {
                throw RequestException(authCodeDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }
    }

    /**
     * Проверяет код на валидность в текущей сессии.
     *
     * Если код правильный, то будет возвращен true. Если код не правильный или произойдет прочая ошибка, то будет
     * выкинуто исключение.
     *
     * Если нет соединения с интернетом, то будет возвращено false.
     *
     * @param phone - номер телефона
     * @param code - код подтверждения
     * @param hash - хеш сессии авторизации
     */
    @Throws(RequestException::class)
    fun checkCode(phone: String, code: String, hash: String) = GlobalScope.async(Dispatchers.IO) {
        if (!CODE_REGEX.matches(code)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        try {
            val authCheckCodeDTO = protocolClient.makeRequest<AuthCheckCodeDTO>("auth.checkCode", AuthCheckCodeDTO().apply {
                this.code = code
                this.hash = hash
                this.phone = phone
            }).await()

            if (authCheckCodeDTO.containsError()) {
                if (authCheckCodeDTO.error == Errors.CODE_EXPIRED || authCheckCodeDTO.error == Errors.TOO_MANY_REQUESTS) {
                    authSessionChannel.clear()
                }

                throw RequestException(authCheckCodeDTO.error)
            }

            return@async true
        } catch (e: NetworkException) {
            // Ignore ...
        }

        return@async false
    }

    /**
     * Выполняет вход в аккаунт. По окончанию записывает аккаунт в БД.
     *
     * Если код правильный, то будет возвращен true и создан аккаунт.
     * Если код не правильный или произойдет прочая ошибка, то будет
     * выкинуто исключение.
     *
     * Если нет соединения с интернетом, то будет возвращено false.
     *
     * @param phone - номер телефона
     * @param code - код подтверждения
     * @param hash - хеш сессии авторизации
     */
    @Throws(RequestException::class)
    fun signIn(phone: String, code: String, hash: String) = GlobalScope.async(Dispatchers.IO) {
        if (!CODE_REGEX.matches(code)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        try {
            val authSignInDTO = protocolClient.makeRequest<AuthSignInDTO>("auth.signIn", AuthSignInDTO().apply {
                this.code = code
                this.hash = hash
                this.phone = phone
            }).await()

            if (authSignInDTO.isSuccess()) {
                saveAccountSession(authSignInDTO.token, with(authSignInDTO.user) {
                    User(this.id, this.name, this.nickname, this.photo, this.phone, this.status, this.bio)
                })

                return@async true
            } else {
                if (authSignInDTO.error == Errors.CODE_EXPIRED || authSignInDTO.error == Errors.TOO_MANY_REQUESTS) {
                    authSessionChannel.clear()
                }

                throw RequestException(authSignInDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }

        return@async false
    }

    /**
     * Выполняет регистрацию аккаунта. По окончанию записывает аккаунт в БД.
     *
     * @param phone - номер телефона
     * @param code - код подтверждения
     * @param hash - хеш сессии авторизации
     * @param name - имя нового пользователя
     * @param nickname - никнейм нового пользователя
     */
    @Throws(RequestException::class, RequestRegexException::class)
    fun signUp(phone: String, code: String, hash: String, name: String, nickname: String) = GlobalScope.async(Dispatchers.IO) {
        val filteredName = name.trim().replace(WHITESPACES_REGEX, " ")
        val filteredNickname = nickname.replace(WHITESPACES_REGEX, "")
        val invalidFields = ArrayList<Int>()

        if (!NAME_REGEX.matches(filteredName))
            invalidFields.plusAssign(AUTH_NAME_REGEX_ERROR)
        if (!NICKNAME_REGEX.matches(filteredNickname))
            invalidFields.plusAssign(AUTH_NICKNAME_REGEX_ERROR)
        if (invalidFields.isNotEmpty()) throw RequestRegexException(invalidFields)

        try {
            val authSignUpDTO = protocolClient.makeRequest<AuthSignUpDTO>("auth.signUp", AuthSignUpDTO().apply {
                this.phone = phone
                this.code = code
                this.hash = hash
                this.name = filteredName
                this.nickname = filteredNickname
            }).await()

            if (authSignUpDTO.isSuccess()) {
                saveAccountSession(authSignUpDTO.token, with(authSignUpDTO.user) {
                    User(this.id, this.name, this.nickname, this.photo, this.phone, this.status, this.bio)
                })

                return@async true
            } else {
                if (authSignUpDTO.error == Errors.CODE_EXPIRED
                        || authSignUpDTO.error == Errors.TOO_MANY_REQUESTS
                        || authSignUpDTO.error == Errors.INVALID_ACCOUNT) {

                    authSessionChannel.clear()
                }

                throw RequestException(authSignUpDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }

        return@async false
    }

    /**
     * Устанавливает сессию по токену.
     * Результат возвращается в шину событий.
     *
     * @param token - токен доступа.
     */
    fun installAccountSession(token: String) = GlobalScope.async(Dispatchers.IO) {
        try {
            val authImportDTO = protocolClient.makeRequest<AuthImportDTO>("auth.importToken", AuthImportDTO().apply {
                this.token = token.trim()
            }).await()

            if (authImportDTO.isSuccess()) {
                saveAccountSession(token, with(authImportDTO.user) {
                    User(this.id, this.name, this.nickname, this.photo, this.phone, this.status, this.bio)
                })
            } else if (authImportDTO.error == Errors.INVALID_ACCOUNT) {
                removeAccountSession()
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }
    }

    /**
     * Сохраняет сессию аккаунта, загружает пользователя в БД, ставит ему метку о кешировании.
     *
     * @param token - токен доступа к сессии.
     * @param user - пользователь сессии.
     */
    suspend fun saveAccountSession(token: String, user: User) {
        accountRepository.saveOrUpdateAccount(token, user)
        usersRepository.saveOrUpdateUsers(user).await()
        usersRepository.loadedUsersIds.plusAssign(user.uid)
        notifyAccountSessionValid()
    }

    /**
     * Девалидирует сесссию аккаунта и удаляет аккаунт из хранилища.
     */
    fun removeAccountSession() {
        accountRepository.removeAccounts()
        notifyAccountSessionInvalid()
    }

    /**
     * Уведомляет пользователей репозитория о том, что сессия установлена.
     *
     * 1) Переключает метку активности сессии.
     * 2) Уведомляет слушателей событий, что сессия установлена.
     * 3) Уничтожает сессию авторизации если она существует.
     */
    fun notifyAccountSessionValid() {
        sessionInstalled = true
        accountSessionStateChannel.offer(true)
        accountSessionStateChannel.clear()
        authSessionChannel.clear()
    }

    /**
     * Уведомляет пользователей репозитория о том, что сессия невалидна.
     *
     * 1) Переключает метку активности сессии.
     * 2) Уведомляет слушателей событий, что сессия невалидна.
     * 3) Уничтожает сессию авторизации если она существует.
     */
    fun notifyAccountSessionInvalid() {
        sessionInstalled = false
        accountSessionStateChannel.offer(false)
        accountSessionStateChannel.clear()
        authSessionChannel.clear()
    }

    /**
     * Сообщает о теоритической возможности выполнения запроса без вылета сессии.
     *
     * Учитывайте, что возможнсть теоретическая => нужно обрабатывать обрыв соединения во время запроса
     */
    fun canExecuteNetworkRequest() = sessionInstalled && protocolClient.isValid()

    /**
     * Выполняет запрос только если нет установленного соединения,
     * в противном случае кидает в шину событий протокола эвент отсутствия соединения.
     *
     * Одна из мер предосторожности при отправке запроса от имени юзера!
     */
    @Throws(NetworkException::class)
    suspend inline fun <reified T : JsonModel> makeRequestWithSession(protocolClient: ProtocolClient, event: String, message: JsonModel? = null, notifyToEventBus: Boolean = true): Deferred<T> {
        if (!canExecuteNetworkRequest()) {
            if (notifyToEventBus) {
                protocolClient.connectionStateChannel.offer(ConnectionState.NO_CONNECTION)
            }

            // Throw NetworkException
            return GlobalScope.async(coroutineContext) {
                suspendCoroutine<T> { coroutine -> coroutine.resumeWithException(NetworkException()) }
            }
        }

        return protocolClient.makeRequest(event, message)
    }
}