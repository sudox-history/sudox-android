package ru.sudox.android.core.inject

import android.os.Bundle
import ru.sudox.android.core.inject.scopes.CoreActivityScope
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.core.managers.ScreenManager
import dagger.Module
import dagger.Provides
import ru.sudox.android.core.CoreFragment
import ru.sudox.android.core.managers.NewNavigationManager

@Module
class CoreActivityModule(
        newNavigationManager: NewNavigationManager,
        screenManager: ScreenManager
) {
    val newNavigationManager: NewNavigationManager? = newNavigationManager
        @Provides
        @CoreActivityScope
        get

    val navigationManager: NavigationManager? = object : NavigationManager {
        override fun showAuthPart() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun showMainPart() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun configureNavigationBar() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun showChildFragment(fragment: CoreFragment) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun popBackstack(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun restoreState(bundle: Bundle): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun saveState(bundle: Bundle) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
        @Provides
        @CoreActivityScope
        get

    val screenManager: ScreenManager? = screenManager
        @Provides
        @CoreActivityScope
        get
}