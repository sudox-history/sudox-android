package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import kotlinx.android.synthetic.main.include_navbar.view.*

class NavigationBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

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
        inflate(context, R.layout.include_navbar, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Configure components
        configureComponents()
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.NavigationBar)

        // Read parameters
        backButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_backButtonIsVisible, false)
        nextButtonIsVisible = array.getBoolean(R.styleable.NavigationBar_nextButtonIsVisible, false)
        sudoxTagIsVisible = array.getBoolean(R.styleable.NavigationBar_sudoxTagIsVisible, false)
        sendAgainIsVisible = array.getBoolean(R.styleable.NavigationBar_sendAgainIsVisible, false)
        backButtonText = array.getString(R.styleable.NavigationBar_backButtonText)
        nextButtonText = array.getString(R.styleable.NavigationBar_nextButtonText)

        // Recycle typed array
        array.recycle()
    }

    @VisibleForTesting
    internal fun configureComponents() {
        configureButton(button_navbar_back, backButtonIsVisible, backButtonText, NavigationAction.BACK)
        configureButton(button_navbar_next, nextButtonIsVisible, nextButtonText, NavigationAction.NEXT)
        configureButton(button_navbar_send_again, sendAgainIsVisible, sendAgainText, NavigationAction.SEND_AGAIN)
        configureText(sudox_tag, sudoxTagIsVisible)
    }


    fun setText(view: AppCompatTextView, text: String){
        view.text = text
    }

    fun setClickable(view: AppCompatTextView, clickable: Boolean){
        view.isClickable = clickable
    }

    private fun configureText(view: AppCompatTextView, visibility: Boolean){
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