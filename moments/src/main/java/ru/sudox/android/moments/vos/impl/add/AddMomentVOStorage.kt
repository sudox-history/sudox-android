package ru.sudox.android.moments.vos.impl.add

import android.graphics.Paint
import android.graphics.drawable.Drawable

data class AddMomentVOStorage(
        val clipColor: Int,
        val clipRadius: Float,
        val strokeRadius: Float,
        val strokeColor: Int,
        val drawable: Drawable,
        val paint: Paint
)