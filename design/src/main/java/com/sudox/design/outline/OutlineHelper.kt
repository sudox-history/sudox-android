package com.sudox.design.outline

import android.graphics.Outline
import android.os.Build

val offsettedOutlineProvider = OffsettedOutlineProvider()

private val OUTLINE_RADIUS_FIELD by lazy {
    Outline::class.java
            .getDeclaredField("mRadius")
            .apply { isAccessible = true }
}

fun Outline.getBorderRadius(): Float {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return radius
    }

    return OUTLINE_RADIUS_FIELD.getFloat(this)
}