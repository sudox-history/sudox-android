package com.sudox.android.data.repositories.auth

import com.sudox.android.common.helpers.*
import com.sudox.android.data.auth.SudoxAccount
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.auth.dto.*
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.protocol.ProtocolClient
import com.sudox.android.data.RequestException
import com.sudox.android.data.RequestRegexException
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
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

    init {
        // Для отслеживания "смерти сессии" (прилетит первым, ибо AuthRepository - первый репозиторий, который инициализируется)
        listenConnectionState()

        // Отслеживаем ошибку unauthorized
        protocolClient.listenErrorCodes {
            if (it == Errors.UNAUTHORIZED) killAccountSession()
        }
    }

    private fun listenConnectionState() = GlobalScope.launch(Dispatchers.IO) {
        protocolClient
                .connectionStateChannel
                .openSubscription()
                .filter { it == ConnectionState.HANDSHAKE_SUCCEED }
                .consumeEach {
                    val account = accountRepository.getAccount().await()

                    // Если нет аккаунта, то нет и сессии ...
                    if (account == null) {
                        accountSessionStateChannel.send(false)
                    } else {
                        importAuth(account.id, account.secret).await()
                    }
                }
    }

    /**
     * Убивает сесссию аккаунта и удаляет аккаунт из хранилища
     **/
    private fun killAccountSession() = GlobalScope.launch(Dispatchers.IO) {
        accountRepository.removeAccounts().await()
        accountSessionStateChannel.send(false)
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
            authSessionChannel.send(AuthSession(phoneNumber, authCodeDTO.hash, authCodeDTO.status))
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
            accountRepository.saveAccount(SudoxAccount(authSignInDTO.id, phoneNumber, authSignInDTO.secret)).await()
            accountSessionStateChannel.send(true)
        } else {
            throw RequestException(authSignInDTO.error)
        }
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
            accountRepository.saveAccount(SudoxAccount(authSignUpDTO.id, phoneNumber, authSignUpDTO.secret)).await()
            accountSessionStateChannel.send(true)
        } else {
            throw RequestException(authSignUpDTO.error)
        }
    }

    /**
     * Импортирует сессию в соединение. Результат возвращает в LiveData.
     **/
    @Suppress("NAME_SHADOWING")
    fun importAuth(id: String, secret: String) = GlobalScope.async(Dispatchers.IO) {
        val id = id.trim()
        val secret = secret.trim()

        val authImportDTO = protocolClient.makeRequest<AuthImportDTO>("auth.importAuth", AuthImportDTO().apply {
            this.id = id
            this.secret = secret
        }).await()

        if (authImportDTO.isSuccess()) {
            accountSessionStateChannel.send(true)
        } else {
            killAccountSession()
        }
    }
}