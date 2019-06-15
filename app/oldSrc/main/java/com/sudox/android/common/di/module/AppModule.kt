package com.sudox.android.common.di.module

import android.app.Application
import android.content.Context
import com.sudox.protocol.ProtocolClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun providesApplication(): Context = app

    @Provides
    @Singleton
    fun providesProtocolClient(): ProtocolClient = ProtocolClient()
}