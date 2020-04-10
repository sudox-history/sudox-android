package ru.sudox.android.auth.repositories

import io.reactivex.rxjava3.core.Observable
import io.realm.Realm
import ru.sudox.android.auth.objects.AuthSessionObject
import ru.sudox.api.auth.AuthService
import ru.sudox.api.auth.entries.create.AuthCreateResponseBody
import ru.sudox.api.common.SudoxApi
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
        val sudoxApi: SudoxApi,
        val authService: AuthService,
        val realm: Provider<Realm>
) {

    fun createSession(userPhone: String): Observable<AuthCreateResponseBody> {
        return authService
                .createSession(userPhone)
                .doOnNext { dto ->
                    realm.get().executeTransaction {
                        it.insert(AuthSessionObject(userPhone, dto.authToken, dto.userExists))
                    }
                }
    }
}