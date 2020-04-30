package ru.sudox.android.account.inject

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.sudox.android.account.AccountAuthenticator
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
class AccountModule(
        private val authActivity: Class<*>
) {

    @Provides
    @Singleton
    fun provideAuthenticator(@Named(APP_CONTEXT_NAME) context: Context): AccountAuthenticator {
        return AccountAuthenticator(context, authActivity)
    }
}