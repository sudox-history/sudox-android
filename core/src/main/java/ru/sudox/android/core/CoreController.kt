package ru.sudox.android.core

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import ru.sudox.android.core.managers.NewNavigationManager
import javax.inject.Inject

private const val CORE_CONTROLLER_ROOT_VIEW_ID_KEY = "core_controller_root_view_id"

abstract class CoreController : Controller() {

    @Inject
    @JvmField
    var navigationManager: NewNavigationManager? = null

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)

        (activity as CoreActivity)
                .getActivityComponent()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        return view ?: createView(container, savedViewState).apply {
            id = savedViewState
                    ?.getInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, View.generateViewId())
                    ?: View.generateViewId()
        }
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        outState.putInt(CORE_CONTROLLER_ROOT_VIEW_ID_KEY, view.id)
    }

    abstract fun createView(container: ViewGroup, savedViewState: Bundle?): View
}