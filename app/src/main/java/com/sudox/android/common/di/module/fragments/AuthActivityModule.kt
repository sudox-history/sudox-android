package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.phone.AuthPhoneFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthActivityModule {

    @ContributesAndroidInjector
    abstract fun provideAuthEmailFragment(): AuthPhoneFragment

    @ContributesAndroidInjector
    abstract fun provideAuthConfirmFragment(): AuthConfirmFragment

    @ContributesAndroidInjector
    abstract fun provideAuthRegisterFragment(): AuthRegisterFragment
}