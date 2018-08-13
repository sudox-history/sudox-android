package com.sudox.android.di.module

import android.accounts.AccountManager
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.main.ContactsRepository
import com.sudox.android.database.ContactsDao
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

    @Provides
    @Singleton
    fun provideContactsRepository(protocolClient: ProtocolClient,
                                  contactsDao: ContactsDao,
                                  accountRepository: AccountRepository) = ContactsRepository(protocolClient, contactsDao, accountRepository)
}