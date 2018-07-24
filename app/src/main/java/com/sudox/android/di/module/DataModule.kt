package com.sudox.android.di.module

import com.sudox.android.repository.AccountRepository
import com.sudox.protocol.ProtocolClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideAccountRepository(protocolClient: ProtocolClient) = AccountRepository(protocolClient)
}