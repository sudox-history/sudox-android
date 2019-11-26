package com.sudox.messenger.android.managers

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import com.sudox.design.applicationBar.ApplicationBar
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.applicationBar.applicationBarButton.ApplicationBarButton
import com.sudox.design.applicationBar.applicationBarButton.ApplicationBarButtonIconDirection
import com.sudox.messenger.android.R
import com.sudox.messenger.android.core.managers.APPBAR_NEXT_BUTTON_INDEX
import com.sudox.messenger.android.core.managers.ApplicationBarManager

class AppApplicationBarManager(
        val activity: Activity,
        val applicationBar: ApplicationBar
) : ApplicationBarManager {

    override fun getButtonStart(): ApplicationBarButton {
        return applicationBar.buttonAtStart!!
    }

    override fun getButtonNext(): ApplicationBarButton {
        return applicationBar.buttonsAtEnd[APPBAR_NEXT_BUTTON_INDEX]!!
    }

    override fun toggleButtonBack(toggle: Boolean) {
        getButtonStart().let {
            if (toggle) {
                it.setIconDrawable(R.drawable.ic_left_arrow)
                it.setIconDirection(ApplicationBarButtonIconDirection.START)
                it.setOnClickListener { activity.onKeyDown(KeyEvent.KEYCODE_BACK, null) }
                it.visibility = View.VISIBLE
                it.isClickable = true
            } else {
                it.setOnClickListener(applicationBar)
                it.reset()
            }
        }
    }

    override fun toggleButtonNext(toggle: Boolean) {
        getButtonNext().let {
            if (toggle) {
                it.setIconDirection(ApplicationBarButtonIconDirection.END)
                it.setText(R.string.next)
                it.visibility = View.VISIBLE
                it.isClickable = true
            } else {
                it.reset()
            }
        }
    }

    override fun setListener(listener: ApplicationBarListener?) {
        applicationBar.listener = listener
    }

    override fun setTitleText(titleId: Int) {
        applicationBar.setTitleText(titleId)
    }

    override fun reset() {
        applicationBar.let {
            it.buttonAtStart!!.setOnClickListener(it)
            it.reset()
        }
    }
}