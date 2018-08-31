package com.sudox.android.di.module

import android.accounts.AccountManager
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.android.common.repository.main.ContactsRepository
import com.sudox.android.database.SudoxDatabase
import com.sudox.android.database.dao.ContactsDao
import com.sudox.android.database.dao.MessagesDao
import com.sudox.protocol.ProtocolClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideAccountRepository(protocolClient: ProtocolClient,
                                 accountManager: AccountManager,
                                 sudoxDatabase: SudoxDatabase) = AccountRepository(protocolClient, accountManager, sudoxDatabase)

    @Provides
    @Singleton
    fun provideContactsRepository(protocolClient: ProtocolClient,
                                  contactsDao: ContactsDao) = ContactsRepository(protocolClient, contactsDao)

    @Provides
    @Singleton
    fun provideMessagesRepository(protocolClient: ProtocolClient,
                                  messagesDao: MessagesDao) = MessagesRepository(protocolClient, messagesDao)
}