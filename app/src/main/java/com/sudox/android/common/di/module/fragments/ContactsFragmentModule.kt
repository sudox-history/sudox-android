package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContactsFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideContactAddFragment(): ContactAddFragment
}