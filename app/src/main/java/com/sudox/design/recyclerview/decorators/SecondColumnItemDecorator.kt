package com.sudox.design.recyclerview.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.sudox.android.R

class SecondColumnItemDecorator(val context: Context) : RecyclerView.ItemDecoration() {

    private val divider: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_item)!!

    /**
     * Метод для отрисовки делителя.
     * Отрисовка производится после расчета места отрисовки.
     */
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        // Draw decorators
        for (i in 0..childCount) {
            val child = parent.getChildAt(i)

            // Calculate coordinates for drawing
            if (child is ViewGroup) {
                val top = child.bottom
                val bottom = child.bottom + divider.intrinsicHeight

                // Second column coordinates
                val secondColumn = child.getChildAt(1)
                val left = secondColumn.left
                val right = secondColumn.right

                // Drawing
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }
    }
}