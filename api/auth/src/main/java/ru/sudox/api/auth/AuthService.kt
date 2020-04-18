package ru.sudox.api.auth

import io.reactivex.Observable
import ru.sudox.api.auth.entries.code.AuthCheckCodeRequestBody
import ru.sudox.api.auth.entries.create.AuthCreateRequestBody
import ru.sudox.api.auth.entries.create.AuthCreateResponseBody
import ru.sudox.api.auth.entries.restore.AuthRestoreRequestBody
import ru.sudox.api.common.SudoxApi

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
    fun createSession(userPhone: String): Observable<AuthCreateResponseBody> {
        return sudoxApi
                .sendRequest("auth.create", AuthCreateRequestBody(userPhone), AuthCreateResponseBody::class.java)
                .doOnNext { lastAuthToken = it.authToken }
                .doOnError { lastAuthToken = null }
    }

    /**
     * Возобновляет сессию авторизации
     *
     * @param authToken Токен сессии авторизации
     * @return Single с ответом от сервера
     */
    fun restoreSession(authToken: String): Observable<Unit> {
        return sudoxApi
                .sendRequest("auth.createSession", AuthRestoreRequestBody(authToken), Unit::class.java)
                .doOnNext { lastAuthToken = authToken }
                .doOnError { lastAuthToken = null }
    }

    /**
     * Уничтожает сессию авторизации
     *
     * @return Single с ответом от сервера.
     */
    fun destroySession(): Observable<Nothing> {
        return sudoxApi
                .sendRequest("auth.destroySession", Unit, Nothing::class.java)
                .doOnNext { lastAuthToken = null }
    }

    /**
     * Проверяет код, введенный пользователем.
     *
     * @param code Код, введенный пользователем
     * @return Single с ответом от сервера.
     */
    fun checkCode(code: Int): Observable<Nothing> {
        return sudoxApi.sendRequest("auth.checkCode", AuthCheckCodeRequestBody(code), Nothing::class.java)
    }
}