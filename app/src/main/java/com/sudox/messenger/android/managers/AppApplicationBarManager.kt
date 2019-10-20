package com.sudox.messenger.android.managers

import com.sudox.design.applicationBar.ApplicationBar
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.managers.ApplicationBarManager

class AppApplicationBarManager(val applicationBar: ApplicationBar) : ApplicationBarManager {

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

    override fun reset() {
        applicationBar.reset()
    }
}