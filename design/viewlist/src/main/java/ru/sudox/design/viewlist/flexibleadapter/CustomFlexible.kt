package ru.sudox.design.viewlist.flexibleadapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

interface CustomFlexible<VH : RecyclerView.ViewHolder> : IFlexible<VH> {
    fun createViewHolder(context: Context, adapter: FlexibleAdapter<*>): VH
}