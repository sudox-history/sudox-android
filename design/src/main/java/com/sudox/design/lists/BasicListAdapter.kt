package com.sudox.design.lists

import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

abstract class BasicListAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    abstract fun createItemViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemViewHolder = createItemViewHolder(parent, viewType)

        if (parent is BasicRecyclerView) {
            val itemView = itemViewHolder.itemView

            itemView.updatePadding(
                        left = itemView.paddingLeft + parent.initialPaddingLeft,
                        right = itemView.paddingRight + parent.initialPaddingRight
                )
        }

        return itemViewHolder
    }
}