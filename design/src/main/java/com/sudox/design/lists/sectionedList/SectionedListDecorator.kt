package com.sudox.design.lists.sectionedList

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SectionedListDecorator(
        var listView: SectionedListView
) : RecyclerView.ItemDecoration() {

    private var sectionNameBounds = Rect()

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = listView.adapter as? SectionedListAdapter<*> ?: return

        for (i in 0 until listView.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            val sectionName = adapter.getSectionName(position) ?: continue

            val textY = child.top - listView.sectionNameBottomPadding.toFloat()
            val textX = listView.initialPaddingLeft.toFloat()

            canvas.drawText(sectionName, textX, textY, listView.sectionNamePaint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val adapter = listView.adapter as? SectionedListAdapter<*> ?: return
        val position = parent.getChildAdapterPosition(view)

        if (position == RecyclerView.NO_POSITION) {
            return
        }

        val sectionName = adapter.getSectionName(position)
        val sectionMargin = adapter.getSectionItemsMargin(position)

        if (sectionName != null) {
            listView.sectionNamePaint.getTextBounds(sectionName, 0, sectionName.length, sectionNameBounds)

            outRect.top = listView.sectionNameTopPadding
        }

        outRect.bottom += sectionMargin
    }
}