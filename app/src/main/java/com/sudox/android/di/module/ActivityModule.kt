package com.sudox.android.di.module

import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector()
    abstract fun bindSplashActivity() : SplashActivity

    @ContributesAndroidInjector(modules = [(AuthActivityModule::class)])
    abstract fun bindAuthActivity() : AuthActivity
}