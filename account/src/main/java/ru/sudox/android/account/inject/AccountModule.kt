package ru.sudox.android.account.inject

import android.accounts.AccountManager
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import ru.sudox.android.account.AccountAuthenticator
import ru.sudox.android.account.repository.AccountRepository
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
class AccountModule(
        private val authActivity: Class<*>,
        private val objectMapper: ObjectMapper,
        private val accountType: String
) {

    @Provides
    @Singleton
    fun provideAccountRepository(accountManager: AccountManager): AccountRepository {
        return AccountRepository(objectMapper, accountManager, accountType)
    }

    @Provides
    @Singleton
    fun provideAccountManager(@Named(APP_CONTEXT_NAME) context: Context): AccountManager {
        return AccountManager.get(context)
    }

    @Provides
    @Singleton
    fun provideAuthenticator(@Named(APP_CONTEXT_NAME) context: Context, accountRepository: AccountRepository): AccountAuthenticator {
        return AccountAuthenticator(context, authActivity, accountRepository)
    }
}