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

        if (position < adapter.itemCount - 1) {
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                outRect.top = itemMargin
            } else {
                outRect.right = itemMargin
            }
        }
    }
}