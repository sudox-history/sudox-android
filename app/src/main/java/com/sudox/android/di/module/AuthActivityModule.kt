package com.sudox.android.di.module

import com.sudox.android.ui.auth.AuthConfirmFragment
import com.sudox.android.ui.auth.AuthEmailFragment
import dagger.Module
import dagger.Provides

@Module
class AuthActivityModule {

    @Provides
    fun provideAuthEmailFragment() = AuthEmailFragment()

    @Provides
    fun provideAuthConfirmFragment() = AuthConfirmFragment()
}