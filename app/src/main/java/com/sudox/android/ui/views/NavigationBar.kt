package com.sudox.android.ui.views

import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.sudox.android.R
import com.sudox.android.ui.views.enums.NavigationAction
import kotlinx.android.synthetic.main.include_auth_navbar.view.*

class NavigationBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    // Back button
    var backButtonIsVisible: Boolean = false
    var backButtonText: String? = null

    // Next button
    var nextButtonIsVisible: Boolean = false
    var nextButtonText: String? = null

    // Sudox tag
    var sudoxTagIsVisible: Boolean = false

    // Some feature
    var someFeatureButtonIsVisible: Boolean = false
    var someFeatureText: String? = null

    // Callback
    var navigationActionCallback: ((NavigationAction) -> (Unit))? = null

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
        val array = context.obtainStyledAttributes(attrs, R.styleable.NavigationBar)

        // Read parameters
        try {
            backButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_backButtonIsVisible, false)
            nextButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_nextButtonIsVisible, false)
            sudoxTagIsVisible = array.getBoolean(R.styleable.NavigationBar_sudoxTagIsVisible, false)
            someFeatureButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_someFeatureButtonIsVisible, false)
            backButtonText = array.getString(R.styleable.NavigationBar_backButtonText)
            nextButtonText = array.getString(R.styleable.NavigationBar_nextButtonText)
        } finally {
            // Recycle typed array
            array.recycle()
        }
    }

    @VisibleForTesting
    internal fun configureComponents() {
        configureButton(buttonNavbarBack, backButtonIsVisible, backButtonText, NavigationAction.BACK)
        configureButton(buttonNavbarNext, nextButtonIsVisible, nextButtonText, NavigationAction.NEXT)
        configureButton(buttonSomeFeature, someFeatureButtonIsVisible, someFeatureText, NavigationAction.SOME_FEATURE)
        configureText(textSudoxTag, sudoxTagIsVisible)
    }

    fun setText(view: AppCompatTextView, text: String) {
        view.text = text
    }

    fun setClickable(view: AppCompatTextView, clickable: Boolean) {
        view.isClickable = clickable
    }

    fun freeze() {
        buttonNavbarBack.isEnabled = false
        buttonNavbarNext.isEnabled = false
        buttonSomeFeature.isEnabled = false
    }

    fun unfreeze() {
        buttonNavbarBack.isEnabled = true
        buttonNavbarNext.isEnabled = true
        buttonSomeFeature.isEnabled = true
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
            view.setOnClickListener { navigationActionCallback?.invoke(action) }
        } else {
            view.visibility = View.GONE
        }
    }
}