package com.sudox.android.di.module.fragments

import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun provideContactsFragment(): ContactsFragment

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment
}