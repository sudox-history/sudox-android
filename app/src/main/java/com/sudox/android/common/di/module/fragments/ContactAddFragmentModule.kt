package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.main.contacts.add.InviteFriendDialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContactAddFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideInviteFriendDialogFragment(): InviteFriendDialogFragment

}