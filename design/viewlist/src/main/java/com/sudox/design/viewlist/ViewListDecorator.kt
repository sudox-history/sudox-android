package com.sudox.design.viewlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewListDecorator(
        private val adapter: ViewListAdapter<*>,
        private val list: ViewList
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutManager = list.layoutManager as? LinearLayoutManager ?: return
        val position = parent.getChildAdapterPosition(view)

        if (adapter.getHeadersCount() > 0 || adapter.getFooterCount() > 0 || !adapter.canCreateMarginViaDecorators(position)) {
            return
        }

        val itemMargin = adapter.getItemMargin(position)

        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
            if (position == 0) {
                outRect.bottom = itemMargin / 2
            } else {
                outRect.top = itemMargin / 2
                outRect.bottom = itemMargin / 2
            }
        } else {
            if (position == 0) {
                outRect.right = itemMargin / 2
            } else {
                outRect.left = itemMargin / 2
                outRect.right = itemMargin / 2
            }
        }
    }
}