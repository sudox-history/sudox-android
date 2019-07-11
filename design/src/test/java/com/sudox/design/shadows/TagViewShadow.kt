package com.sudox.design.shadows

import android.view.View
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowView

@Implements(View::class)
class TagViewShadow : ShadowView() {

    private var tag: Any? = null

    @Implementation
    fun setTag(tag: Any?) {
        this.tag = tag
    }

    @Implementation
    fun getTag(): Any? {
        return tag!!
    }
}