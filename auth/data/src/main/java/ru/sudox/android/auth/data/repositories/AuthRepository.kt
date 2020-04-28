package ru.sudox.android.auth.data.repositories

import ru.sudox.android.auth.data.daos.AuthSessionDAO
import ru.sudox.api.auth.AuthService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        val authService: AuthService,
        val authSessionDAO: AuthSessionDAO
) {


}