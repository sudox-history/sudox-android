package ru.sudox.android.core.controllers

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import ru.sudox.android.core.CoreController

private const val SCROLLABLE_CONTROLLER_CHILD_VIEW_ID = "scrollable_controller_child_view_id"

abstract class ScrollableController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return ScrollView(activity).apply {
            addView(createChildView(container, savedViewState).apply {
                id = savedViewState
                        ?.getInt(SCROLLABLE_CONTROLLER_CHILD_VIEW_ID, View.generateViewId())
                        ?: View.generateViewId()
            })
        }
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