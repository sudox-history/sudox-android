package ru.sudox.api.inject

import com.fasterxml.jackson.databind.ObjectMapper
import ru.sudox.api.SudoxApiImpl
import ru.sudox.api.connections.Connection
import dagger.Module
import dagger.Provides
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import ru.sudox.api.auth.AuthService
import ru.sudox.api.common.SudoxApi
import javax.inject.Singleton

@Module
class ApiModule(
        private val connection: Connection,
        private val objectMapper: ObjectMapper,
        private val phoneNumberUtil: PhoneNumberUtil
) {

    @Singleton
    @Provides
    fun provideSudoxApi(): SudoxApi {
        return SudoxApiImpl(connection, objectMapper)
    }

    @Singleton
    @Provides
    fun provideAuthService(api: SudoxApi): AuthService {
        return AuthService(api, phoneNumberUtil)
    }
}