package com.sudox.messenger.android.managers

import android.app.Activity
import com.sudox.design.applicationBar.ApplicationBar
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.R
import com.sudox.messenger.android.core.managers.ApplicationBarManager

class AppApplicationBarManager(
        val applicationBar: ApplicationBar,
        val activity: Activity
) : ApplicationBarManager {

    override fun setTitle(titleTextId: Int) {
        applicationBar.setTitle(titleTextId)
    }

    override fun setListener(listener: ApplicationBarListener) {
        applicationBar.listener = listener
    }

    override fun showButtonAtStart(iconDrawableId: Int) {
        applicationBar.buttonAtStart!!.toggle(iconDrawableId)
    }

    override fun showButtonAtEnd(iconDrawableId: Int) {
        applicationBar.buttonAtEnd!!.toggle(iconDrawableId)
    }

    override fun showBackButton() {
        applicationBar.buttonAtStart!!.toggle(R.drawable.ic_left_arrow)
        applicationBar.buttonAtStart!!.setOnClickListener { activity.onBackPressed() }
    }

    override fun reset() {
        applicationBar.reset()
    }
}