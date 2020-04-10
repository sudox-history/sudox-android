package ru.sudox.android.auth.repositories

import io.objectbox.BoxStore
import io.reactivex.rxjava3.core.Observable
import ru.sudox.android.database.entities.auth.AuthSessionEntity
import ru.sudox.android.database.helpers.observableCreate
import ru.sudox.api.auth.AuthService
import ru.sudox.api.common.SudoxApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        val sudoxApi: SudoxApi,
        val authService: AuthService,
        val boxStore: BoxStore
) {

    fun createSession(userPhone: String): Observable<AuthSessionEntity> {
        return authService
                .createSession(userPhone)
                .flatMap {
                    observableCreate(boxStore.boxFor(AuthSessionEntity::class.java), AuthSessionEntity(
                            phoneNumber = userPhone,
                            userExists = it.userExists,
                            token = it.authToken
                    ))
                }
    }
}