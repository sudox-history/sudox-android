package ru.sudox.android.core.inject

import ru.sudox.android.core.inject.scopes.CoreActivityScope
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.core.managers.ScreenManager
import dagger.Module
import dagger.Provides
import ru.sudox.android.core.managers.NewNavigationManager

@Module
class CoreActivityModule(
        navigationManager: NavigationManager,
        screenManager: ScreenManager
) {
    val newNavigationManager: NewNavigationManager? = null
        @Provides
        @CoreActivityScope
        get

    val navigationManager: NavigationManager? = navigationManager
        @Provides
        @CoreActivityScope
        get

    val screenManager: ScreenManager? = screenManager
        @Provides
        @CoreActivityScope
        get
}