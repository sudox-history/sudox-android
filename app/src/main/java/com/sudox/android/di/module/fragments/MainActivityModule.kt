package com.sudox.android.di.module.fragments

import com.sudox.android.ui.main.ContactsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun provideContactsFragment(): ContactsFragment
}