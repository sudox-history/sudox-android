package com.sudox.api.inject

import com.fasterxml.jackson.databind.ObjectMapper
import com.sudox.api.SudoxApi
import com.sudox.api.connections.Connection
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

    var api = SudoxApi(connection, objectMapper)
        @Singleton
        @Provides
        get
}