package com.sudox.design.editTextLayout

import android.view.View

interface EditTextLayoutChild {
    fun getInstance(): View
    fun setStroke(width: Int, color: Int)
}