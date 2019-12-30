package com.sudox.design.sectionedList

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SectionedListDecorator(
        var listView: SectionedListView
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        (listView.adapter as? SectionedListAdapter<*>)?.let {
            val position = parent.getChildAdapterPosition(view)

            if (position == RecyclerView.NO_POSITION) {
                return
            }
        }
    }
}