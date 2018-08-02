package com.sudox.android.di.module.fragments

import com.sudox.android.ui.auth.email.AuthEmailFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthActivityModule {

    @ContributesAndroidInjector
    abstract fun provideAuthEmailFragment(): AuthEmailFragment

    //TODO: Add two fragments
}