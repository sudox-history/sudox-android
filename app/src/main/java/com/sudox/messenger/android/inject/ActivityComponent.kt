package com.sudox.messenger.android.inject

import com.sudox.messenger.android.AppActivity
import com.sudox.messenger.android.core.inject.CoreActivityComponent
import com.sudox.messenger.android.core.inject.CoreActivityModule
import com.sudox.messenger.android.core.inject.scopes.CoreActivityScope
import dagger.Subcomponent

@CoreActivityScope
@Subcomponent(modules = [CoreActivityModule::class])
interface ActivityComponent : CoreActivityComponent {
    fun inject(appActivity: AppActivity)
}