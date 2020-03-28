package com.sudox.messenger.android.core.inject

import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.core.managers.OldNavigationManager
import com.sudox.messenger.android.core.managers.ScreenManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule(
        navigationManager: NavigationManager?,
        screenManager: ScreenManager
) {

    var navigationManager: NavigationManager? = navigationManager
        @Provides
        @Singleton
        get

    val oldNavigationManager: OldNavigationManager? = null
        @Provides
        @Singleton
        @kotlin.Deprecated(message = "Not working! Will be removed")
        get

    val screenManager: ScreenManager? = screenManager
        @Provides
        @Singleton
        get
}