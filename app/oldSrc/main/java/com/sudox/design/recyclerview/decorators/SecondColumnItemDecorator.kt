package com.sudox.design.recyclerview.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.sudox.android.R

class SecondColumnItemDecorator(val context: Context, val showOnLatestElement: Boolean = true, val showBeforeLatestElement: Boolean = true) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    private val divider: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_item)!!

    /**
     * Метод для отрисовки делителя.
     * Отрисовка производится после расчета места отрисовки.
     */
    override fun onDraw(canvas: Canvas, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val childCount = parent.childCount

        // Draw decorators
        for (i in 0 until childCount) {
            if (i == childCount - 1 && !showOnLatestElement) break
            if (i == childCount - 2 && !showBeforeLatestElement) continue

            // Элемент под которым нужно нарисовать Divider
            val child = parent.getChildAt(i)

            // Calculate coordinates for drawing
            if (child is ViewGroup) {
                val top = child.bottom
                val bottom = child.bottom + divider.intrinsicHeight

                // Second column coordinates
                val secondColumn = child.getChildAt(1)
                val left = secondColumn.left + parent.paddingLeft
                val right = secondColumn.right + parent.paddingRight

                // Drawing
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }
    }
}