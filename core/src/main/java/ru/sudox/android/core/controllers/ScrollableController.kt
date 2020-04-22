package ru.sudox.android.core.controllers

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.CoreController
import ru.sudox.android.core.views.RestorableScrollView

private const val SCROLLABLE_CONTROLLER_CHILD_VIEW_ID = "scrollable_controller_child_view_id"

abstract class ScrollableController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return RestorableScrollView(activity!!).apply {
            setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY > 0) {
                    appBarManager!!.requestElevationToggling(toggle = true, animate = true)
                } else {
                    appBarManager!!.requestElevationToggling(toggle = false, animate = true)
                }
            }

            addView(createChildView(container, savedViewState).apply {
                id = savedViewState
                        ?.getInt(SCROLLABLE_CONTROLLER_CHILD_VIEW_ID, View.generateViewId())
                        ?: View.generateViewId()
            })
        }
    }

    override fun isInStartState(): Boolean {
        return (view as RestorableScrollView).scrollY == 0
    }

    override fun toStartState() {
        (view as RestorableScrollView).scrollTo(0, 0)
    }

    override fun getViewForBind(parent: View): View {
        return (parent as ViewGroup).getChildAt(0)
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState.apply {
            if ((view as ViewGroup).childCount == 1) {
                putInt(SCROLLABLE_CONTROLLER_CHILD_VIEW_ID, view.getChildAt(0).id)
            }
        })
    }

    abstract fun createChildView(container: ViewGroup, savedViewState: Bundle?): View
}