package ru.sudox.android.managers

import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.core.managers.AUTH_ROOT_TAG
import ru.sudox.android.core.managers.NewNavigationManager

class AppNavigationManager(
        val router: Router,
        val bottomNavigationView: BottomNavigationView
) : NewNavigationManager {

    override fun clearBackstack() {
    }

    override fun popBackstack(): Boolean {
        return router.popCurrentController()
    }

    override fun showChild(controller: Controller) {
        router.pushController(RouterTransaction.with(controller))
    }

    override fun showRoot(tag: Int) {
        if (tag == AUTH_ROOT_TAG) {
            bottomNavigationView.visibility = View.GONE
        }


    }
}