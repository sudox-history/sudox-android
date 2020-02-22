package com.sudox.messenger.android.core.inject

import com.sudox.messenger.android.core.CoreFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent {
    fun inject(coreFragment: CoreFragment)
}