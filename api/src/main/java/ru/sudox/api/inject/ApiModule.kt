package ru.sudox.api.inject

import com.fasterxml.jackson.databind.ObjectMapper
import ru.sudox.api.SudoxApiImpl
import ru.sudox.api.connections.Connection
import dagger.Module
import dagger.Provides
import ru.sudox.api.auth.AuthService
import ru.sudox.api.common.SudoxApi
import javax.inject.Singleton

@Module
class ApiModule(
        connection: Connection,
        objectMapper: ObjectMapper
) {

    var connection = connection
        @Singleton
        @Provides
        get

    var objectMapper = objectMapper
        @Singleton
        @Provides
        get

    var api: SudoxApi = SudoxApiImpl(connection, objectMapper)
        @Singleton
        @Provides
        get

    @Singleton
    @Provides
    fun provideAuthService(api: SudoxApi): AuthService {
        return AuthService(api)
    }
}