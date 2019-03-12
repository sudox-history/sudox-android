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
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val AUTH_NAME_REGEX_ERROR = 0
const val AUTH_NICKNAME_REGEX_ERROR = 1

@Singleton
class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val accountRepository: AccountRepository) {

    @JvmField
    var accountSessionStateChannel: ConflatedBroadcastChannel<Boolean> = ConflatedBroadcastChannel()
    val authSessionChannel: ConflatedBroadcastChannel<AuthSession> = ConflatedBroadcastChannel()
    var isSessionInstalled: Boolean = false

    // Начало костыля для избежания циклического инжекта
    lateinit var usersRepository: UsersRepository

    /**
     * /x/x/x/x/x/
     * /x/x/x/x/x/
     * /x/x/x/x/x/
     * /x/x/x/x/x/ x/x/x/x/x/
     * /x/x/x/x/x/ x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x/ x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x/ x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                  /x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                                  /x/x/x/x/x/x/x/x/x/x                                                              /x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                                             /x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x     /x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                                                   /x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x     /x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                                                   /x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x     /x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                                             /x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x     /x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                                  /x/x/x/x/x/x/x/x/x/x                                                              /x/x/x/x/x/x
     * /x/x/x/x/x                            /x/x/x                                 /x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x/ x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x/ x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x/ x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x/x
     * /x/x/x/x/x/ x/x/x/x/x/
     * /x/x/x/x/x/
     * /x/x/x/x/x/
     * /x/x/x/x/x/
     *
     * Комментарий от TheMax: пришлось вынести в отдельный метод, ибо бывали случаи,
     * что usersRepository не успевал инициализироваться и в итоге вылетал NullPointerException.
     *
     * 29.12.2018, 02:24
     */
    fun init(usersRepository: UsersRepository) {
        this.usersRepository = usersRepository

        // Начинаем работу ...
        listenConnectionState()

        // Отслеживаем ошибку unauthorized
        protocolClient.listenErrorCodes {
            if (it == Errors.UNAUTHORIZED) killAccountSession()
        }
    }

    // Конец костыля для избежания циклического инжекта

    private fun listenConnectionState() = GlobalScope.launch(Dispatchers.IO) {
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
                        importAuth(token)
                    } else {
                        killAccountSession()
                    }
                } else {
                    notifyAccountSessionInvalid()
                }
            }
        }
    }

    /**
     * Убивает сесссию аккаунта и удаляет аккаунт из хранилища
     **/
    private fun killAccountSession() = GlobalScope.launch(Dispatchers.IO) {
        accountRepository.removeAccounts()
        notifyAccountSessionInvalid()
    }

    private fun notifyAccountSessionInvalid() {
        isSessionInstalled = false
        accountSessionStateChannel.offer(false)
        accountSessionStateChannel.clear()
        authSessionChannel.clear()
    }

    private fun notifyAccountSessionValid() {
        isSessionInstalled = true
        accountSessionStateChannel.offer(true)
        accountSessionStateChannel.clear()
        authSessionChannel.clear()
    }

    /**
     * Метод, запрашивающий отправку кода подтверждения на почту и уведомляющий сервер о начале
     * сессии авторизации.
     * **/
    @Suppress("NAME_SHADOWING")
    @Throws(RequestException::class)
    fun requestCode(phone: String) = GlobalScope.async(Dispatchers.IO) {
        if (!PHONE_REGEX.matches(phone)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        // Ок, запрашиваем отправку кода ...
        val authCodeDTO = protocolClient.makeRequest<AuthCodeDTO>("auth.sendCode", AuthCodeDTO().apply {
            this.phone = phone
        }).await()

        // Началась сессия авторизации ...
        if (authCodeDTO.isSuccess()) {
            authSessionChannel.offer(AuthSession(phone, authCodeDTO.hash, authCodeDTO.status))
        } else {
            throw RequestException(authCodeDTO.error)
        }

        return@async authCodeDTO
    }

    /**
     * Метод, проверяющий валидность введенного кода в данной сессии авторизации
     * **/
    @Suppress("NAME_SHADOWING")
    fun checkCode(phone: String, code: String, hash: String) = GlobalScope.async(Dispatchers.IO) {
        val code = code.trim()

        // Проверяем валидность кода ...
        if (!NUMBER_REGEX.matches(code)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        val authCheckCodeDTO = protocolClient.makeRequest<AuthCheckCodeDTO>("auth.checkCode", AuthCheckCodeDTO().apply {
            this.code = code
            this.hash = hash
            this.phone = phone
        }).await()

        // Ошибка ...
        if (authCheckCodeDTO.containsError()) {
            throw RequestException(authCheckCodeDTO.error)
        }
    }

    /**
     * Выполняет вход в аккаунт. По окончанию записывает аккаунт в БД.
     */
    @Suppress("NAME_SHADOWING")
    @Throws(RequestException::class)
    fun signIn(phone: String, code: String, hash: String) = GlobalScope.async(Dispatchers.IO) {
        val code = code.trim()

        // Проверяем валидность кода ...
        if (!NUMBER_REGEX.matches(code)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        val authSignInDTO = protocolClient.makeRequest<AuthSignInDTO>("auth.signIn", AuthSignInDTO().apply {
            this.code = code
            this.hash = hash
            this.phone = phone
        }).await()

        if (authSignInDTO.isSuccess()) {
            val user = with(authSignInDTO.user) {
                User(this.id, this.name, this.nickname, this.photo, this.phone, this.status, this.bio)
            }

            accountRepository.saveAccount(authSignInDTO.token, user)
            usersRepository.loadedUsersIds.plusAssign(user.uid)
            usersRepository.saveOrUpdateUsers(user).await()
            notifyAccountSessionValid()
        } else {
            throw RequestException(authSignInDTO.error)
        }
    }

    /**
     * Выполняет регистрацию аккаунта. По окончанию записывает аккаунт в БД.
     */
    @Suppress("NAME_SHADOWING")
    @Throws(RequestException::class, RequestRegexException::class)
    fun signUp(phone: String, code: String, hash: String, name: String, nickname: String) = GlobalScope.async(Dispatchers.IO) {
        val name = name.trim().replace(WHITESPACES_REGEX, " ")
        val nickname = nickname.replace(WHITESPACES_REGEX, "")
        val invalidFields by lazy { arrayListOf<Int>() }

        if (!NAME_REGEX.matches(name))
            invalidFields.plusAssign(AUTH_NAME_REGEX_ERROR)
        if (!NICKNAME_REGEX.matches(nickname))
            invalidFields.plusAssign(AUTH_NICKNAME_REGEX_ERROR)

        // Есть ошибки формата.
        if (invalidFields.isNotEmpty()) {
            throw RequestRegexException(invalidFields)
        }

        val authSignUpDTO = protocolClient.makeRequest<AuthSignUpDTO>("auth.signUp", AuthSignUpDTO().apply {
            this.phone = phone
            this.code = code
            this.hash = hash
            this.name = name
            this.nickname = nickname
        }).await()

        if (authSignUpDTO.isSuccess()) {
            val user = with(authSignUpDTO.user) {
                User(this.id, this.name, this.nickname, this.photo, this.phone, this.status, this.bio)
            }

            accountRepository.saveAccount(authSignUpDTO.token, user)
            usersRepository.loadedUsersIds.plusAssign(user.uid)
            usersRepository.saveOrUpdateUsers(user).await()
            notifyAccountSessionValid()
        } else {
            throw RequestException(authSignUpDTO.error)
        }
    }

    /**
     * Импортирует сессию в соединение. Результат возвращает в LiveData.
     **/
    @Suppress("NAME_SHADOWING")
    fun importAuth(token: String) = GlobalScope.launch(Dispatchers.IO) {
        val authImportDTO = protocolClient.makeRequest<AuthImportDTO>("auth.importToken", AuthImportDTO().apply {
            this.token = token.trim()
        }).await()

        if (authImportDTO.isSuccess()) {
            val user = with(authImportDTO.user) {
                User(this.id, this.name, this.nickname, this.photo, this.phone, this.status, this.bio)
            }

            accountRepository.saveAccount(authImportDTO.token, user)
            usersRepository.loadedUsersIds.plusAssign(user.uid)
            usersRepository.saveOrUpdateUsers(user).await()
            notifyAccountSessionValid()
        } else {
            killAccountSession()
        }
    }

    /**
     * Сообщает о теоритической возможности выполнения запроса без вылета сессии.
     *
     * Учитывайте, что возможнсть теоретическая => нужно обрабатывать обрыв соединения во время запроса
     */
    fun canExecuteNetworkRequest() = isSessionInstalled && protocolClient.isValid()

    /**
     * Выполняет запрос только если нет установленного соединения,
     * в противном случае кидает в шину событий протокола эвент отсутствия соединения.
     *
     * Одна из мер предосторожности при отправке запроса от имени юзера!
     */
    @Throws(NetworkException::class)
    suspend inline fun <reified T : JsonModel> makeRequestWithSession(protocolClient: ProtocolClient, event: String, message: JsonModel? = null, notifyToEventBus: Boolean = true): Deferred<T> {
        return if (canExecuteNetworkRequest()) {
            protocolClient.makeRequestWithControl(event, message)
        } else {
            if (notifyToEventBus) {
                protocolClient.connectionStateChannel.offer(ConnectionState.CONNECTION_CLOSED)
            }

            // Return NetworkException
            GlobalScope.async(coroutineContext) {
                suspendCoroutine<T> { coroutine -> coroutine.resumeWithException(NetworkException()) }
            }
        }
    }
}