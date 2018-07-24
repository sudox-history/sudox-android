package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.sudox.android.R
import kotlinx.android.synthetic.main.include_navbar.view.*

class NavigationBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    // TODO: Сделать отработку событий нажатия на кнопки и анимацию

    // Back button
    var backButtonIsVisible: Boolean = false
    var backButtonText: String? = null

    // Next button
    var nextButtonIsVisible: Boolean = false
    var nextButtonText: String? = null

    init {
        readAttrs(attrs)

        // Inflate view
        val view = inflate(context, R.layout.include_navbar, this)

        // Configure components
        configureComponents(view)
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.NavigationBar)

        // Read parameters
        backButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_backButtonIsVisible, false)
        nextButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_nextButtonIsVisible, false)
        backButtonText = array.getString(R.styleable.NavigationBar_backButtonText)
        nextButtonText = array.getString(R.styleable.NavigationBar_nextButtonText)

        // Recycle typed array
        array.recycle()
    }

    private fun configureComponents(view: View) {
        if (backButtonIsVisible && backButtonText != null) {
            val buttonToolbarBack = view.buttonToolbarBack

            // Show this button
            buttonToolbarBack.visibility = View.VISIBLE
        }

        if (nextButtonIsVisible && nextButtonText != null) {
            val buttonToolbarNext = view.buttonToolbarNext

            // Show this button
            buttonToolbarNext.visibility = View.VISIBLE
        }
    }
}