package com.sudox.android.di.module

import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.email.AuthEmailFragment
import dagger.Module
import dagger.Provides

@Module
class AuthActivityModule {

    @Provides
    fun provideAuthEmailFragment() = AuthEmailFragment()

    @Provides
    fun provideAuthConfirmFragment() = AuthConfirmFragment()
}