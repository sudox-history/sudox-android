package com.sudox.design

import android.view.View

fun View.isLayoutRtl(): Boolean {
    return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL || layoutDirection == View.LAYOUT_DIRECTION_RTL
}