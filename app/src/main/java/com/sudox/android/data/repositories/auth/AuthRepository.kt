package com.sudox.android.data.repositories.auth

import com.sudox.android.ApplicationLoader
import com.sudox.android.common.helpers.*
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.auth.SudoxAccount
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.auth.dto.*
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject
import javax.inject.Singleton

const val AUTH_NAME_REGEX_ERROR = 0
const val AUTH_NICKNAME_REGEX_ERROR = 1

@Singleton
class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val accountRepository: AccountRepository) {

    // Шины
    val authSessionChannel: ConflatedBroadcastChannel<AuthSession> = ConflatedBroadcastChannel()
    val accountSessionStateChannel: ConflatedBroadcastChannel<Boolean> = ConflatedBroadcastChannel()
    var currentUserChannel: ConflatedBroadcastChannel<User> = ConflatedBroadcastChannel()
    var sessionIsValid: Boolean = false

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
        val initialAccount = accountRepository
                .getAccount()
                .await()

        if (initialAccount != null) {
            val user = usersRepository
                    .loadUser(initialAccount.id, onlyFromDatabase = true)
                    .await()

            if (user != null) currentUserChannel.offer(user)
        }

        for (state in protocolClient.connectionStateChannel.openSubscription()) {
            if (state == ConnectionState.HANDSHAKE_SUCCEED) {
                val account = accountRepository.getAccount().await()

                // Если нет аккаунта, то нет и сессии ...
                if (account == null) {
                    notifyAccountSessionInvalid()
                } else {
                    importAuth(account.id, account.secret)
                }
            }
        }
    }

    /**
     * Убивает сесссию аккаунта и удаляет аккаунт из хранилища
     **/
    private fun killAccountSession() = GlobalScope.launch(Dispatchers.IO) {
        accountRepository.removeAccounts().await()
        notifyAccountSessionInvalid()
    }

    private fun notifyAccountSessionInvalid() {
        accountSessionStateChannel.offer(false)
        sessionIsValid = false
    }

    private fun notifyAccountSessionValid() {
        accountSessionStateChannel.offer(true)
        sessionIsValid = true
    }

    /**
     * Метод, запрашивающий отправку кода подтверждения на почту и уведомляющий сервер о начале
     * сессии авторизации.
     * **/
    @Suppress("NAME_SHADOWING")
    @Throws(RequestException::class)
    fun requestCode(phoneNumber: String) = GlobalScope.async(Dispatchers.IO) {
        if (!PHONE_REGEX.matches(phoneNumber)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        // Ок, запрашиваем отправку кода ...
        val authCodeDTO = protocolClient.makeRequest<AuthCodeDTO>("auth.sendCode", AuthCodeDTO().apply {
            this.phoneNumber = phoneNumber
        }).await()

        // Началась сессия авторизации ...
        if (authCodeDTO.isSuccess())
            authSessionChannel.offer(AuthSession(phoneNumber, authCodeDTO.hash, authCodeDTO.status))
        else
            throw RequestException(authCodeDTO.error)

        return@async authCodeDTO
    }

    /**
     * Метод, проверяющий валидность введенного кода в данной сессии авторизации
     * **/
    @Suppress("NAME_SHADOWING")
    fun checkCode(phoneNumber: String, code: String, hash: String) = GlobalScope.async(Dispatchers.IO) {
        val code = code.trim()

        // Проверяем валидность кода ...
        if (!NUMBER_REGEX.matches(code)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        val authCheckCodeDTO = protocolClient.makeRequest<AuthCheckCodeDTO>("auth.checkCode", AuthCheckCodeDTO().apply {
            this.code = code
            this.hash = hash
            this.phoneNumber = phoneNumber
        }).await()

        // Ошибка ...
        if (authCheckCodeDTO.containsError())
            throw RequestException(authCheckCodeDTO.error)
    }

    /**
     * Выполняет вход в аккаунт. По окончанию записывает аккаунт в БД.
     */
    @Suppress("NAME_SHADOWING")
    @Throws(RequestException::class)
    fun signIn(phoneNumber: String, code: String, hash: String) = GlobalScope.async(Dispatchers.IO) {
        val code = code.trim()

        // Проверяем валидность кода ...
        if (!NUMBER_REGEX.matches(code)) {
            throw RequestException(Errors.INVALID_PARAMETERS)
        }

        val authSignInDTO = protocolClient.makeRequest<AuthSignInDTO>("auth.signIn", AuthSignInDTO().apply {
            this.code = code
            this.hash = hash
            this.phoneNumber = phoneNumber
        }).await()

        if (authSignInDTO.isSuccess()) {
            val user = loadCurrentUser(authSignInDTO.id)

            // Save current account ...
            accountRepository.saveAccount(SudoxAccount(authSignInDTO.id, phoneNumber, authSignInDTO.secret))
                    .await()

            notifyAccountSessionValid()
        } else {
            throw RequestException(authSignInDTO.error)
        }
    }

    @Throws(RequestException::class)
    private suspend fun loadCurrentUser(userId: Long) {
        val user = usersRepository
                .loadUser(userId)
                .await() ?: return

        // Notify subscribers ...
        currentUserChannel.offer(user)
    }

    /**
     * Выполняет регистрацию аккаунта. По окончанию записывает аккаунт в БД.
     */
    @Suppress("NAME_SHADOWING")
    @Throws(RequestException::class, RequestRegexException::class)
    fun signUp(phoneNumber: String, code: String, hash: String, name: String, nickname: String) = GlobalScope.async(Dispatchers.IO) {
        val name = name.trim().replace(WHITESPACES_REMOVE_REGEX, " ")
        val nickname = nickname.replace(WHITESPACES_REMOVE_REGEX, "")
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
            this.phoneNumber = phoneNumber
            this.code = code
            this.hash = hash
            this.name = name
            this.nickname = nickname
        }).await()

        if (authSignUpDTO.isSuccess()) {
            val user = loadCurrentUser(authSignUpDTO.id)

            // Save current account ...
            accountRepository.saveAccount(SudoxAccount(authSignUpDTO.id, phoneNumber, authSignUpDTO.secret))
                    .await()

            notifyAccountSessionValid()
        } else {
            throw RequestException(authSignUpDTO.error)
        }
    }

    /**
     * Импортирует сессию в соединение. Результат возвращает в LiveData.
     **/
    @Suppress("NAME_SHADOWING")
    fun importAuth(id: Long, secret: String) = GlobalScope.launch(Dispatchers.IO) {
        val secret = secret.trim()

        val authImportDTO = protocolClient.makeRequest<AuthImportDTO>("auth.quickSignIn", AuthImportDTO().apply {
            this.id = id
            this.secret = secret
        }).await()

        if (authImportDTO.isSuccess()) {
            loadCurrentUser(id)
            notifyAccountSessionValid()
        } else {
            killAccountSession()
        }
    }
}