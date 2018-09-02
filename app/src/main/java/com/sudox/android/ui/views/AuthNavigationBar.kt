package com.sudox.android.ui.views

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import kotlinx.android.synthetic.main.include_auth_navbar.view.*

class AuthNavigationBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    // Back button
    var backButtonIsVisible: Boolean = false
    var backButtonText: String? = null

    // Next button
    var nextButtonIsVisible: Boolean = false
    var nextButtonText: String? = null

    // Sudox tag
    var sudoxTagIsVisible: Boolean = false

    // Send again
    var sendAgainIsVisible: Boolean = false
    var sendAgainText: String? = null

    // Live data
    var navigationLiveData: MutableLiveData<NavigationAction> = MutableLiveData()

    init {
        readAttrs(attrs)

        // Inflate view
        inflate(context, R.layout.include_auth_navbar, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Configure components
        configureComponents()
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.AuthNavigationBar)

        // Read parameters
        try {
            backButtonIsVisible = array.getBoolean(R.styleable.AuthNavigationBar_backButtonIsVisible, false)
            nextButtonIsVisible = array.getBoolean(R.styleable.AuthNavigationBar_nextButtonIsVisible, false)
            sudoxTagIsVisible = array.getBoolean(R.styleable.AuthNavigationBar_sudoxTagIsVisible, false)
            sendAgainIsVisible = array.getBoolean(R.styleable.AuthNavigationBar_sendAgainIsVisible, false)
            backButtonText = array.getString(R.styleable.AuthNavigationBar_backButtonText)
            nextButtonText = array.getString(R.styleable.AuthNavigationBar_nextButtonText)
        } finally {
            // Recycle typed array
            array.recycle()
        }
    }

    @VisibleForTesting
    internal fun configureComponents() {
        configureButton(buttonNavbarBack, backButtonIsVisible, backButtonText, NavigationAction.BACK)
        configureButton(buttonNavbarNext, nextButtonIsVisible, nextButtonText, NavigationAction.NEXT)
        configureButton(buttonSomeFeature, sendAgainIsVisible, sendAgainText, NavigationAction.SEND_AGAIN)
        configureText(textSudoxTag, sudoxTagIsVisible)
    }


    fun setText(view: AppCompatTextView, text: String) {
        view.text = text
    }

    fun setClickable(view: AppCompatTextView, clickable: Boolean) {
        view.isClickable = clickable
    }

    private fun configureText(view: AppCompatTextView, visibility: Boolean) {
        if (visibility) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    private fun configureButton(view: AppCompatTextView, visibility: Boolean, text: String?, action: NavigationAction) {
        if (visibility) {
            view.visibility = View.VISIBLE
            view.isClickable = true

            // Set text
            if (text != null) {
                view.text = text
            }

            // Create click listener
            view.setOnClickListener { navigationLiveData.postValue(action) }
        } else {
            view.visibility = View.GONE
        }
    }
}