package com.sudox.android.common.di.module.activities

import com.sudox.android.ui.messages.dialog.DialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MessagesActivityModule {

    @ContributesAndroidInjector
    abstract fun provideUserChatFragment(): DialogFragment
}
