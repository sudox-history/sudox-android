package ru.sudox.android.people

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListContainer

class ProfileController : ViewListController<ViewListAdapter<*>>() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return ViewListContainer(activity!!).apply {
            viewList = super.createView(container, savedViewState) as ViewList
        }
    }

    override fun getAdapter(viewList: ViewList): ViewListAdapter<*>? {
        return object : ViewListAdapter<InternalHolder>() {
            override fun createItemHolder(parent: ViewGroup, viewType: Int): InternalHolder {
                return InternalHolder(AppCompatTextView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    gravity = Gravity.CENTER_HORIZONTAL
                })
            }

            override fun bindItemHolder(holder: InternalHolder, position: Int) {
                if (position % 10 == 0) {
                    holder.view.text = "Header at $position"
                } else {
                    holder.view.text = "Item at $position"
                }
            }

            override fun getStickyView(context: Context): View {
                return AppCompatTextView(context)
            }

            override fun isViewCanProvideData(view: View): Boolean {
                return true
            }

            override fun isViewCanBeSticky(view: View): Boolean {
                return (view as AppCompatTextView).text.startsWith("Header")
            }

            override fun bindStickyView(view: View, provider: View) {
                val textView = provider as AppCompatTextView
                val text = textView.text.toString()
                val position = if (textView.text.startsWith("Header")) {
                    text.removePrefix("Header at ").toInt()
                } else {
                    text.removePrefix("Item at ").toInt() / 10 * 10
                }

                (view as AppCompatTextView).text = "Header at $position"
            }

            override fun getItemsCountAfterHeader(type: Int): Int {
                return 100
            }
        }
    }

    class InternalHolder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)
}
