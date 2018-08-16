package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.sudox.android.R

class SearchAdditionalView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    init {
        inflate(context, R.layout.include_search_navbar_addition, this)
    }

}