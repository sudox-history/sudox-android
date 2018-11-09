package com.sudox.android.data.repositories.auth

import android.arch.lifecycle.MutableLiveData
import com.sudox.android.common.helpers.*
import com.sudox.android.data.auth.SudoxAccount
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.account.state.AccountSessionState
import com.sudox.android.data.models.auth.dto.*
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

const val AUTH_NAME_REGEX_ERROR = 0
const val AUTH_NICKNAME_REGEX_ERROR = 1

@Singleton
class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val accountRepository: AccountRepository) {

    // Шины
    val authSessionLiveData: MutableLiveData<AuthSession> = SingleLiveEvent()
    val accountSessionLiveData: MutableLiveData<AccountSessionState> = SingleLiveEvent()

    init {
        // Для отслеживания "смерти сессии" (прилетит первым, ибо AuthRepository - первый репозиторий, который инициализируется)
        protocolClient.connectionStateLiveData.observeForever {
            runBlocking {
                if (it != ConnectionState.HANDSHAKE_SUCCEED) return@runBlocking

                // Импорт сессии если есть данные в AccountRepository
                val account = accountRepository.getAccount().await()

                // Если нет аккаунта, то нет и сессии ...
                if (account != null) {
                    importAuth(account.id, account.secret)
                } else {
                    accountSessionLiveData.postValue(AccountSessionState(false))
                }
            }
        }

        // Отслеживаем ошибку unauthorized
        protocolClient.listenErrorCodes {
            if (it == Errors.UNAUTHORIZED) killAccountSession()
        }
    }

    /**
     * Убивает сесссию аккаунта и удаляет аккаунт из хранилища
     **/
    private fun killAccountSession() = GlobalScope.async {
        accountRepository.removeAccounts().await()
        accountSessionLiveData.postValue(AccountSessionState(false))
    }

    /**
     * Метод, запрашивающий отправку кода подтверждения на почту и уведомляющий сервер о начале
     * сессии авторизации.
     * **/
    @Suppress("NAME_SHADOWING")
    fun requestCode(phoneNumber: String, successCallback: (AuthCodeDTO) -> Unit, errorCallback: (Int) -> Unit) {
        // "Кастрируем" пробелы в почте ...
        val phoneNumberL = phoneNumber.replace(WHITESPACES_REMOVE_REGEX, "")

        // Проверка на валидность формата почты (для экономии трафика производим проверку ещё на клиенте)
        if (!PHONE_REGEX.matches(phoneNumberL)) {
            errorCallback(Errors.INVALID_PARAMETERS)
        } else {
            // Ок, запрашиваем отправку кода ...
            protocolClient.makeRequest<AuthCodeDTO>("auth.sendCode", AuthCodeDTO().apply {
                this.phoneNumber = phoneNumberL
            }) {
                if (it.isSuccess()) {
                    authSessionLiveData.postValue(AuthSession(phoneNumberL, it.hash, it.status))
                    successCallback(it)
                } else {
                    errorCallback(it.error)
                }
            }
        }
    }

    /**
     * Метод, проверяющий валидность введенного кода в данной сессии авторизации
     * **/
    @Suppress("NAME_SHADOWING")
    fun checkCode(phoneNumber: String, code: String, hash: String, successCallback: () -> (Unit), errorCallback: (Int) -> (Unit)) {
        // "Кастрируем" пробелы в почте ...
        val code = code.trim()

        // Проверяем валидность кода ...
        if (!NUMBER_REGEX.matches(code)) {
            // Защита от отличных от Gboard клавиатур (желательно было бы заблокировать ввод левых символов)
            errorCallback(Errors.INVALID_PARAMETERS)
        } else {
            protocolClient.makeRequest<AuthCheckCodeDTO>("auth.checkCode", AuthCheckCodeDTO().apply {
                this.code = code
                this.hash = hash
                this.phoneNumber = phoneNumber
            }) {
                if (it.isSuccess()) {
                    successCallback()
                } else {
                    errorCallback(it.error)
                }
            }
        }
    }

    /**
     * Выполняет вход в аккаунт. По окончанию записывает аккаунт в БД.
     */
    @Suppress("NAME_SHADOWING")
    fun signIn(phoneNumber: String, code: String, hash: String, successCallback: () -> (Unit), errorCallback: (Int) -> (Unit)) {
        // "Кастрируем" пробелы в почте ...
        val code = code.trim()

        // Проверяем валидность кода ...
        if (!NUMBER_REGEX.matches(code)) {
            // Защита от отличных от Gboard клавиатур (желательно было бы заблокировать ввод левых символов)
            errorCallback(Errors.INVALID_PARAMETERS)
        } else {
            protocolClient.makeRequest<AuthSignInDTO>("auth.signIn", AuthSignInDTO().apply {
                this.code = code
                this.hash = hash
                this.phoneNumber = phoneNumber
            }) {
                if (it.isSuccess()) {
                    accountRepository.saveAccount(SudoxAccount(it.id, phoneNumber, it.secret))
                    accountSessionLiveData.postValue(AccountSessionState(true))
                    successCallback()
                } else {
                    errorCallback(it.error)
                }
            }
        }
    }

    /**
     * Выполняет регистрацию аккаунта. По окончанию записывает аккаунт в БД.
     */
    @Suppress("NAME_SHADOWING")
    fun signUp(phoneNumber: String, code: String, hash: String, name: String, nickname: String,
               regexCallback: (List<Int>) -> (Unit),
               successCallback: () -> (Unit),
               errorCallback: (Int) -> (Unit)) = GlobalScope.async {
        val name = name.trim().replace(WHITESPACES_REMOVE_REGEX, " ")
        val nickname = nickname.replace(WHITESPACES_REMOVE_REGEX, "")
        val regexErrors = arrayListOf<Int>()

        // Валидация
        if (!NAME_REGEX.matches(name)) {
            regexErrors.plusAssign(AUTH_NAME_REGEX_ERROR)
        }

        if (!NICKNAME_REGEX.matches(nickname)) {
            regexErrors.plusAssign(AUTH_NICKNAME_REGEX_ERROR)
        }

        // Fix bug with single validation
        if (regexErrors.isNotEmpty()) {
            regexCallback(regexErrors)
            return@async
        }

        protocolClient.makeRequest<AuthSignUpDTO>("auth.signUp", AuthSignUpDTO().apply {
            this.phoneNumber = phoneNumber
            this.code = code
            this.hash = hash
            this.name = name
            this.nickname = nickname
        }) {
            if (it.isSuccess()) {
                accountRepository.saveAccount(SudoxAccount(it.id, phoneNumber, it.secret))
                accountSessionLiveData.postValue(AccountSessionState(true))
                successCallback()
            } else {
                errorCallback(it.error)
            }
        }
    }

    /**
     * Импортирует сессию в соединение. Результат возвращает в LiveData.
     **/
    @Suppress("NAME_SHADOWING")
    fun importAuth(id: String, secret: String) {
        val id = id.trim()
        val secret = secret.trim()

        // Пробуем установить сессию
        protocolClient.makeRequest<AuthImportDTO>("auth.importAuth", AuthImportDTO().apply {
            this.id = id
            this.secret = secret
        }) {
            if (it.isSuccess()) {
                accountSessionLiveData.postValue(AccountSessionState(true))
            } else {
                killAccountSession()
            }
        }
    }
}