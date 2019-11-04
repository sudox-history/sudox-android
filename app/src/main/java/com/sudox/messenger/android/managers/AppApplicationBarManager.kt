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
        val applicationBar: ApplicationBar,
        val activity: Activity
) : ApplicationBarManager {

    override fun getButtonStart(): ApplicationBarButton {
        return applicationBar.buttonAtStart!!
    }

    override fun getButtonNext(): ApplicationBarButton {
        return applicationBar.buttonsAtEnd[APPBAR_NEXT_BUTTON_INDEX]!!
    }

    override fun toggleButtonBack(toggle: Boolean) {
        val button = applicationBar.buttonAtStart!!

        if (toggle) {
            button.setIconDrawable(R.drawable.ic_left_arrow)
            button.setIconDirection(ApplicationBarButtonIconDirection.START)
            button.setOnClickListener {
                activity.onKeyDown(KeyEvent.KEYCODE_BACK, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
            }

            button.visibility = View.VISIBLE
            button.isClickable = true
        } else {
            button.reset()
        }
    }

    override fun toggleButtonNext(toggle: Boolean) {
        val button = getButtonNext()

        if (toggle) {
            button.setIconDirection(ApplicationBarButtonIconDirection.END)
            button.setText(R.string.next)
            button.visibility = View.VISIBLE
            button.isClickable = true
        } else {
            button.reset()
        }
    }

    override fun setListener(listener: ApplicationBarListener?) {
        applicationBar.listener = listener
    }

    override fun setTitleText(titleId: Int) {
        applicationBar.setTitleText(titleId)
    }

    override fun reset() {
        applicationBar.buttonAtStart!!.setOnClickListener(applicationBar)
        applicationBar.reset()
    }
}