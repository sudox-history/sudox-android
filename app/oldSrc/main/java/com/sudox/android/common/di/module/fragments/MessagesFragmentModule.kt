package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.main.messages.channels.ChannelsFragment
import com.sudox.android.ui.main.messages.dialogs.DialogsFragment
import com.sudox.android.ui.main.messages.talks.TalksFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MessagesFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideDialogsFragment(): DialogsFragment

    @ContributesAndroidInjector
    abstract fun provideTalksFragment(): TalksFragment

    @ContributesAndroidInjector
    abstract fun provideChannelsFragment(): ChannelsFragment
}