package ru.sudox.android.core.inject

import dagger.Module
import dagger.Provides
import ru.sudox.android.core.inject.scopes.CoreActivityScope
import ru.sudox.android.core.managers.NewNavigationManager

@Module
class CoreActivityModule(
        private val navigationManager: NewNavigationManager
) {

    @Provides
    @CoreActivityScope
    fun provideNavigationManager(): NewNavigationManager {
        return navigationManager
    }
}