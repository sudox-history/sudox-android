package ru.sudox.android.core.inject

import dagger.Module
import dagger.Provides
import ru.sudox.android.core.inject.scopes.CoreActivityScope
import ru.sudox.android.core.managers.AppBarManager
import ru.sudox.android.core.managers.NavigationManager

@Module
class CoreActivityModule(
        private val navigationManager: NavigationManager,
        private val appBarManager: AppBarManager
) {

    @Provides
    @CoreActivityScope
    fun provideNavigationManager(): NavigationManager {
        return navigationManager
    }

    @Provides
    @CoreActivityScope
    fun provideAppBarManager(): AppBarManager {
        return appBarManager
    }
}