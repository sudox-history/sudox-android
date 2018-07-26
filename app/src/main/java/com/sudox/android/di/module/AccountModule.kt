package com.sudox.android.di.module

import android.accounts.AccountManager
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AccountModule {

    @Provides
    @Singleton
    fun provideAccountManager(context: Context): AccountManager {
        return AccountManager.get(context)
    }
}