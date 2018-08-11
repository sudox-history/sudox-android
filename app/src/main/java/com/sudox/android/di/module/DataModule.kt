package com.sudox.android.di.module

import android.accounts.AccountManager
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.protocol.ProtocolClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideAccountRepository(protocolClient: ProtocolClient,
                                 accountManager: AccountManager) = AccountRepository(protocolClient, accountManager)
}