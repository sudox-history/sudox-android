package com.sudox.messenger.android.managers

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import com.sudox.design.applicationBar.ApplicationBar
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.applicationBar.applicationBarButton.ApplicationBarButtonIconDirection
import com.sudox.messenger.android.R
import com.sudox.messenger.android.core.managers.APPBAR_NEXT_BUTTON_INDEX
import com.sudox.messenger.android.core.managers.ApplicationBarManager

class AppApplicationBarManager(
        val activity: Activity,
        val applicationBar: ApplicationBar
) : ApplicationBarManager {

    private var buttonAtStart = applicationBar.buttonAtStart!!
    private var buttonAtEnd = applicationBar.buttonsAtEnd[APPBAR_NEXT_BUTTON_INDEX]!!

    override fun toggleTextButtonAtStart(titleId: Int) {
        buttonAtStart.let {
            it.setText(titleId)
            it.setIconDrawable(null)
            it.visibility = View.VISIBLE
            it.isClickable = true
        }
    }

    override fun toggleIconButtonAtStart(iconId: Int) {
        buttonAtStart.let {
            it.setText(null)
            it.setIconDrawable(iconId)
            it.visibility = View.VISIBLE
            it.isClickable = true
        }
    }

    override fun toggleTextButtonAtEnd(titleId: Int) {
        buttonAtEnd.let {
            it.setText(titleId)
            it.setIconDrawable(null)
            it.visibility = View.VISIBLE
            it.isClickable = true
        }
    }

    override fun toggleIconButtonAtEnd(iconId: Int) {
        buttonAtEnd.let {
            it.setText(null)
            it.setIconDrawable(iconId)
            it.visibility = View.VISIBLE
            it.isClickable = true
        }
    }

    override fun toggleButtonBack(toggle: Boolean) {
        buttonAtStart.let {
            if (toggle) {
                it.setIconDrawable(R.drawable.ic_left_arrow)
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
        buttonAtEnd.let {
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

    override fun setTitleText(text: String) {
        applicationBar.setTitleText(text)
    }

    override fun reset() {
        buttonAtStart.setOnClickListener(applicationBar)
        applicationBar.reset()
    }
}