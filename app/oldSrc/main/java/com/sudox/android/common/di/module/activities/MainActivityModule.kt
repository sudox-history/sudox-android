package com.sudox.android.common.di.module.activities

import com.sudox.android.common.di.module.fragments.ContactsFragmentModule
import com.sudox.android.common.di.module.fragments.MessagesFragmentModule
import com.sudox.android.common.di.module.fragments.ProfileFragmentModule
import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.android.ui.main.profile.ProfileFragment
import com.sudox.android.ui.main.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [ContactsFragmentModule::class])
    abstract fun provideContactsFragment(): ContactsFragment

    @ContributesAndroidInjector(modules = [MessagesFragmentModule::class])
    abstract fun provideMessagesFragment(): MessagesFragment

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun provideProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment
}