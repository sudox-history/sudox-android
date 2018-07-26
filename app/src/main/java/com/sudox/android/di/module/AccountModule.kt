package com.sudox.android.di.module

import com.sudox.android.common.SudoxAuthenticator
import com.sudox.android.common.service.SudoxAuthenticatorService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AccountModule {
    @Provides
    @Singleton
    fun provideSudoxAuthenticator(sudoxAuthenticatorService: SudoxAuthenticatorService)
            = SudoxAuthenticator(sudoxAuthenticatorService)
}