package com.sudox.design.lists.sectionedList

import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.BasicListAdapter

abstract class SectionedListAdapter<VH : RecyclerView.ViewHolder> : BasicListAdapter<VH>() {
    abstract fun getSectionName(position: Int): String?
    abstract fun getSectionItemsMargin(position: Int): Int
}