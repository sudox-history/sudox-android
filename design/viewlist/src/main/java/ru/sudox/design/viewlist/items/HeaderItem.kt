package ru.sudox.design.viewlist.items

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.viewholders.FlexibleViewHolder

class HeaderItem<VH : FlexibleViewHolder> : AbstractFlexibleItem<VH>(), IHeader<VH> {

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): VH {
        TODO("Not yet implemented")
    }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
            holder: VH,
            position: Int,
            payloads: MutableList<Any>?
    ) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLayoutRes(): Int {
        TODO("Not yet implemented")
    }
}