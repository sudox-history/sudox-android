package ru.sudox.android.auth.repositories

import io.reactivex.rxjava3.core.Single
import ru.sudox.api.auth.AuthService
import ru.sudox.api.auth.entries.create.AuthCreateResponseBody
import ru.sudox.api.common.SudoxApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        val sudoxApi: SudoxApi,
        val authService: AuthService
) {

    fun createSession(userPhone: String): Single<AuthCreateResponseBody> {
        return authService.createSession(userPhone)
    }
}