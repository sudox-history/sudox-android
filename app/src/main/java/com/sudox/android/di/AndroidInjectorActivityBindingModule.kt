package com.sudox.android.di

import com.sudox.android.ui.activities.AuthActivity
import com.sudox.android.ui.activities.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity() : SplashActivity

    @ContributesAndroidInjector
    abstract fun bindAuthActivity() : AuthActivity
}