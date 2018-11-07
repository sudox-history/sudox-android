package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.android.ui.main.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun provideContactsFragment(): ContactsFragment

    @ContributesAndroidInjector(modules = [MessagesFragmentModule::class])
    abstract fun provideMessagesFragment(): MessagesFragment

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment
}