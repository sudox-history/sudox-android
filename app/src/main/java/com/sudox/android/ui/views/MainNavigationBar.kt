package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import com.sudox.android.R
import com.sudox.android.common.enums.NavigationAction
import kotlinx.android.synthetic.main.include_main_navbar.view.*

class MainNavigationBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    var textTagString: String? = null
    var navigationLiveData: MutableLiveData<NavigationAction> = MutableLiveData()

    init {
        readAttrs(attrs)

        // Inflate view
        inflate(context, R.layout.include_main_navbar, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Configure components
        configureComponents()
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MainNavigationBar)

        try {
            // Read parameters
            textTagString = array.getString(R.styleable.MainNavigationBar_tagText)
        } finally {
            // Recycle typed array
            array.recycle()
        }
    }

    @VisibleForTesting
    private fun configureComponents() {
        configureText(textTag, textTagString)
    }

    private fun configureText(textTag: AppCompatTextView, textTagString: String?) {
        textTag.text = textTagString
    }
}