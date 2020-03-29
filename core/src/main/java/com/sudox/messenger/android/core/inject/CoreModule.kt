package com.sudox.messenger.android.core.inject

import com.sudox.messenger.android.core.managers.NavigationManager
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

    val screenManager: ScreenManager? = screenManager
        @Provides
        @Singleton
        get
}