package com.sudox.messenger.android.core.inject

import com.sudox.messenger.android.core.inject.scopes.CoreActivityScope
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.core.managers.ScreenManager
import dagger.Module
import dagger.Provides

@Module
class CoreActivityModule(
        navigationManager: NavigationManager,
        screenManager: ScreenManager
) {
    val navigationManager: NavigationManager? = navigationManager
        @Provides
        @CoreActivityScope
        get

    val screenManager: ScreenManager? = screenManager
        @Provides
        @CoreActivityScope
        get
}