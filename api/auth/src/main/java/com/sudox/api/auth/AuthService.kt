package com.sudox.api.auth

import com.sudox.api.auth.entries.create.AuthCreateRequestBody
import com.sudox.api.auth.entries.create.AuthCreateResponseBody
import com.sudox.api.auth.entries.restore.AuthRestoreRequestBody
import com.sudox.api.common.SudoxApi
import io.reactivex.rxjava3.core.Single

class AuthService(val sudoxApi: SudoxApi) {

    var lastUserKey: ByteArray? = null
        private set

    var lastAuthToken: String? = null
        private set

    /**
     * Создает сессию авторизации.
     *
     * @param userPhone Телефон пользователя
     * @return Single с ответом от сервера
     */
    fun createSession(userPhone: String): Single<AuthCreateResponseBody> {
        return sudoxApi
                .sendRequest("auth.create", AuthCreateRequestBody(userPhone), AuthCreateResponseBody::class.java)
                .doOnSuccess { lastAuthToken = it.authToken }
                .doOnError { lastAuthToken = null }
    }

    /**
     * Возобновляет сессию авторизации
     *
     * @param authToken Токен сессии авторизации
     * @return Single с ответом от сервера
     */
    fun restoreSession(authToken: String): Single<Unit> {
        return sudoxApi
                .sendRequest("auth.restoreSession", AuthRestoreRequestBody(authToken), Unit::class.java)
                .doOnSuccess { lastAuthToken = authToken }
                .doOnError { lastAuthToken = null }
    }

    /**
     * Уничтожает сессию авторизации
     *
     * @return Single с ответом от сервера.
     */
    fun destroySession(): Single<Nothing> {
        return sudoxApi
                .sendRequest("auth.destroySession", Unit, Nothing::class.java)
                .doOnSuccess { lastAuthToken = null }
    }

    /**
     * Проверяет код, введенный пользователем.
     *
     * @param code Код, введенный пользователем
     * @return Single с ответом от сервера.
     */
    fun checkCode(code: Int): Single<Nothing> {
        return sudoxApi.sendRequest("auth.checkCode", Unit, Nothing::class.java)
    }
}