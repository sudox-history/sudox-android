package com.sudox.design.lists.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
        val marginVertical: Int = 0,
        val marginHorizontal: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.apply {
            if (parent.getChildAdapterPosition(view) != 0) {
                left = marginHorizontal
                top = marginVertical
            }

            right = marginHorizontal
            bottom = marginVertical
        }
    }
}