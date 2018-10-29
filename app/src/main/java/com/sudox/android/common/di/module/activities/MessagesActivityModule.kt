package com.sudox.android.common.di.module.activities

import com.sudox.android.ui.messages.chat.ChatFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MessagesActivityModule {

    @ContributesAndroidInjector
    abstract fun provideUserChatFragment(): ChatFragment
}
