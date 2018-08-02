package com.sudox.android.di.module.fragments

import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.email.AuthEmailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthActivityModule {

    @ContributesAndroidInjector
    abstract fun provideAuthEmailFragment(): AuthEmailFragment

    @ContributesAndroidInjector
    abstract fun provideAuthConfirmFragment(): AuthConfirmFragment

    //TODO: Add two fragments
}