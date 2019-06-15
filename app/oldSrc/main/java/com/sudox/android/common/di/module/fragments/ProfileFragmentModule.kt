package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.main.profile.decorations.ProfileDecorationsFragment
import com.sudox.android.ui.main.profile.info.ProfileInfoFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideProfileInfoFragment(): ProfileInfoFragment

    @ContributesAndroidInjector
    abstract fun provideProfileDecorationsFragment(): ProfileDecorationsFragment
}