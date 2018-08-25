package com.sudox.android.di.module.activities

import com.sudox.android.di.module.fragments.AuthActivityModule
import com.sudox.android.di.module.fragments.MainActivityModule
import com.sudox.android.ui.chats.ChatActivity
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity() : SplashActivity

    @ContributesAndroidInjector(modules = [(AuthActivityModule::class)])
    abstract fun bindAuthActivity() : AuthActivity

    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    abstract fun bindMainActivity() : MainActivity

    @ContributesAndroidInjector
    abstract fun bindChatActivity() : ChatActivity
}