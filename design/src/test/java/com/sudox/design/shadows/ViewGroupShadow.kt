package com.sudox.design.shadows

import android.view.View
import android.view.ViewGroup
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowViewGroup

@Implements(ViewGroup::class)
open class ViewGroupShadow : ShadowViewGroup() {

    private val children = ArrayList<View>()

    @Implementation
    fun addView(child: View, index: Int) {
        if (index == -1) {
            children.add(child)
        } else {
            children.add(index, child)
        }
    }

    @Implementation
    fun indexOfChild(child: View): Int {
        for (i in 0 until children.size) {
            if (children[i] === child) {
                return i
            }
        }

        return -1
    }

    @Implementation
    fun removeView(view: View) {
        removeViewInLayout(view)
    }

    @Implementation
    fun removeViewAt(position: Int) {
        children.removeAt(position)
    }

    @Implementation
    fun removeViewInLayout(view: View) {
        val index = indexOfChild(view)

        if (index >= 0) {
            removeViewAt(index)
        }
    }
}