package com.sudox.android.common.repository.auth

import android.arch.lifecycle.MutableLiveData
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.helpers.*
import com.sudox.android.common.models.Errors
import com.sudox.android.common.models.account.state.AccountSessionState
import com.sudox.android.common.models.auth.dto.*
import com.sudox.android.common.models.auth.state.AuthSession
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                         private val accountRepository: AccountRepository) {

    // Шины
    val authSessionLiveData: MutableLiveData<AuthSession> = SingleLiveEvent()
    val accountSessionLiveData: MutableLiveData<AccountSessionState> = SingleLiveEvent()

    /**
     * Метод, запрашивающий отправку кода подтверждения на почту и уведомляющий сервер о начале
     * сессии авторизации.
     * **/
    @Suppress("NAME_SHADOWING")
    fun requestCode(email: String, callback: (Boolean) -> (Unit)) {
        // "Кастрируем" пробелы в почте ...
        val email = email.replace(WHITESPACES_REMOVE_REGEX, "")

        // Проверка на валидность формата почты (для экономии трафика производим проверку ещё на клиенте)
        if (!EMAIL_REGEX.matches(email)) {
            // Сожалеем, но наше приложение не подходит для взлома левым E-mail'ом :)
            callback(false)
        } else {
            // Ок, запрашиваем отправку кода ...
            protocolClient.makeRequest<AuthCodeDTO>("auth.sendCode", AuthCodeDTO().apply {
                this.email = email
            }) {
                if (it.isSuccess()) {
                    authSessionLiveData.postValue(AuthSession(email, it.hash, it.status))
                }

                // Notify caller about status of code request
                callback(it.isSuccess())
            }
        }
    }

    /**
     * Метод, проверяющий валидность введенного кода в данной сессии авторизации
     * **/
    @Suppress("NAME_SHADOWING")
    fun checkCode(email: String, code: String, hash: String, successCallback: () -> (Unit), errorCallback: (Int) -> (Unit)) {
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
                this.email = email
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
    fun signIn(email: String, code: String, hash: String, successCallback: () -> (Unit), errorCallback: (Int) -> (Unit)) {
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
                this.email = email
            }) {
                if (it.isSuccess()) {
                    accountRepository.saveAccount(SudoxAccount(it.id, email, it.secret))
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
    fun signUp(email: String, code: String, hash: String, name: String, nickname: String, successCallback: () -> (Unit), errorCallback: (Int) -> (Unit)) {
        val name = name.trim().replace(WHITESPACES_REMOVE_REGEX, " ")
        val nickname = nickname.replace(WHITESPACES_REMOVE_REGEX, "")

        // Валидация
        if (!NAME_REGEX.matches(name) || !NICKNAME_REGEX.matches(nickname)) {
            errorCallback(Errors.INVALID_PARAMETERS)
        } else {
            protocolClient.makeRequest<AuthSignUpDTO>("auth.signUp", AuthSignUpDTO().apply {
                this.email = email
                this.code = code
                this.hash = hash
                this.name = name
                this.nickname = nickname
            }) {
                if (it.isSuccess()) {
                    accountRepository.saveAccount(SudoxAccount(it.id, email, it.secret))
                    accountSessionLiveData.postValue(AccountSessionState(true))
                    successCallback()
                } else {
                    errorCallback(it.error)
                }
            }
        }
    }

    /**
     * Стартует сессию по указанным id и secret...
     */
    fun importAuth(id: String, secret: String, callback: (Boolean) -> (Unit)) {
        protocolClient.makeRequest<AuthImportDTO>("auth.importAuth", AuthImportDTO().apply {
            this.id = id
            this.secret = secret
        }) {
            // TODO: Save to account manager
        }
    }
}