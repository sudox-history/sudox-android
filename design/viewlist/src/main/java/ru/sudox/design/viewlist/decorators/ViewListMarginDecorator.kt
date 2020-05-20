package ru.sudox.design.viewlist.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter

class ViewListMarginDecorator(
        private val list: ViewList
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = list.adapter as ViewListAdapter<*>
        val layoutManager = list.layoutManager as? LinearLayoutManager ?: return
        val position = parent.getChildAdapterPosition(view)

        if (position > 0 && adapter.getFooterText(position) != null && !adapter.canCreateMarginViaDecorators()) {
            outRect.top = list.footerMargin - adapter.getItemMargin(position) / 2
            return
        }

        if (adapter.getHeadersCount() > 0 || !adapter.canCreateMarginViaDecorators()) {
            return
        }

        val itemMargin = adapter.getItemMargin(position)
        val range = if (layoutManager.stackFromEnd || layoutManager.reverseLayout) {
            1 until adapter.itemCount
        } else {
            0 until adapter.itemCount - 1
        }

        if (position in range) {
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                outRect.top = itemMargin
            } else {
                outRect.right = itemMargin
            }
        }
    }
}