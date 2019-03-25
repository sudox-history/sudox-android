package com.sudox.android.common.di.module.activities

import com.sudox.android.ui.auth.confirm.AuthConfirmFragment
import com.sudox.android.ui.auth.phone.AuthPhoneFragment
import com.sudox.android.ui.auth.register.AuthRegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthActivityModule {

    @ContributesAndroidInjector
    abstract fun provideAuthPhoneFragment(): AuthPhoneFragment

    @ContributesAndroidInjector
    abstract fun provideAuthConfirmFragment(): AuthConfirmFragment

    @ContributesAndroidInjector
    abstract fun provideAuthRegisterFragment(): AuthRegisterFragment
}