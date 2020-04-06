package ru.sudox.api.inject

import com.fasterxml.jackson.databind.ObjectMapper
import ru.sudox.api.SudoxApiImpl
import ru.sudox.api.connections.Connection
import dagger.Module
import dagger.Provides
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

    var api = SudoxApiImpl(connection, objectMapper)
        @Singleton
        @Provides
        get
}