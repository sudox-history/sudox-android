package com.sudox.design.sectionedList

import androidx.recyclerview.widget.RecyclerView

abstract class SectionedListAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    abstract fun getSectionName(): String?
}