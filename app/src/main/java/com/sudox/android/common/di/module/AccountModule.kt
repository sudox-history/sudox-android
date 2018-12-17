package com.sudox.android.common.di.module

import android.accounts.AccountManager
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AccountModule {

    @Provides
    @Singleton
    fun provideAccountManager(context: Context)
            = AccountManager.get(context)
}