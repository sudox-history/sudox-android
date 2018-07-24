package com.sudox.android.di.module

import com.sudox.android.ui.activity.AuthActivity
import com.sudox.android.ui.activity.splashscreen.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity() : SplashActivity

    @ContributesAndroidInjector
    abstract fun bindAuthActivity() : AuthActivity
}