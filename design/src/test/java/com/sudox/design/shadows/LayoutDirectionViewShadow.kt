package com.sudox.design.shadows

import android.view.View
import android.widget.TextView
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowView

@Implements(View::class)
open class LayoutDirectionViewShadow : ShadowView() {

    private var layoutDirection = View.LAYOUT_DIRECTION_LTR

    @Implementation
    fun getLayoutDirection(): Int {
        return layoutDirection
    }

    @Implementation
    fun setLayoutDirection(direction: Int) {
        layoutDirection = direction
    }
}