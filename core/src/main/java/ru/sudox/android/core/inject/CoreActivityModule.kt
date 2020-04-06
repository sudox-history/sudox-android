package ru.sudox.android.core.inject

import ru.sudox.android.core.inject.scopes.CoreActivityScope
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.core.managers.ScreenManager
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