package com.sudox.api.inject

import com.sudox.api.SudoxApi
import com.sudox.api.connections.Connection
import com.sudox.api.serializers.Serializer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApiModule(
        connection: Connection,
        serializer: Serializer
) {

    var connection = connection
        @Singleton
        @Provides
        get

    var serializer = serializer
        @Singleton
        @Provides
        get

    var api = SudoxApi(connection, serializer)
        @Singleton
        @Provides
        get
}