package com.sudox.design.popup

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.popup.views.PopupItemView
import com.sudox.design.popup.vos.PopupItemVO

class ListPopupAdapter(
        val context: Context,
        val items: List<PopupItemVO<*>>
) : RecyclerView.Adapter<ListPopupAdapter.ViewHolder>() {

    var itemClickedCallback: ((PopupItemVO<*>) -> (Unit))? = null
    var maximumItemWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PopupItemView(context).apply {
            minimumWidth = this@ListPopupAdapter.maximumItemWidth
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.let {
            it.setOnClickListener {
                val activeItem = items.find { item -> item.isActive }
                val clickedItem = items[holder.adapterPosition]

                if (activeItem != clickedItem) {
                    itemClickedCallback!!(clickedItem)
                }
            }

            it.vo = items[position]
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val view: PopupItemView) : RecyclerView.ViewHolder(view)
}