package ru.sudox.android.managers

import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.auth.ui.phone.AuthPhoneController
import ru.sudox.android.core.managers.AUTH_ROOT_TAG
import ru.sudox.android.core.managers.NewNavigationManager
import ru.sudox.android.core.managers.PEOPLE_ROOT_TAG
import ru.sudox.android.people.PeopleController

private const val NAVIGATION_VIEW_VISIBILITY_KEY = "bottom_navigation_view_visibility_key"

class AppNavigationManager(
        val routerProvider: Lazy<Router>,
        val bottomNavigationView: BottomNavigationView
) : NewNavigationManager {

    override fun clearBackstack() {
    }

    override fun popBackstack(): Boolean {
        return routerProvider.value.popCurrentController()
    }

    override fun showChild(controller: Controller) {
        routerProvider.value.pushController(RouterTransaction.with(controller))
    }

    override fun showRoot(tag: Int) {
        if (tag == AUTH_ROOT_TAG) {
            bottomNavigationView.visibility = View.GONE
            routerProvider.value.setRoot(RouterTransaction.with(AuthPhoneController()))
        } else if (tag == PEOPLE_ROOT_TAG) {
            bottomNavigationView.visibility = View.VISIBLE
            routerProvider.value.setRoot(RouterTransaction.with(PeopleController()))
        }
    }

    override fun restoreState(bundle: Bundle?) {
        if (bundle != null) {
            bottomNavigationView.visibility = bundle.getInt(NAVIGATION_VIEW_VISIBILITY_KEY)
        }
    }

    override fun saveState(bundle: Bundle?) {
        bundle?.putInt(NAVIGATION_VIEW_VISIBILITY_KEY, bottomNavigationView.visibility)
    }
}