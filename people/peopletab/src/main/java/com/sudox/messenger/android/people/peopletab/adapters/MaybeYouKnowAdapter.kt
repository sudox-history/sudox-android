package com.sudox.messenger.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.common.views.VerticalPeopleItemView
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.callbacks.MaybeYouKnowSortingCallback
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

class MaybeYouKnowAdapter : ViewListAdapter<MaybeYouKnowAdapter.ViewHolder>(null) {

    val maybeYouKnowVOs = SortedList<MaybeYouKnowVO>(MaybeYouKnowVO::class.java, MaybeYouKnowSortingCallback(this))

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VerticalPeopleItemView(viewList!!.context))
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        holder.view.vo = maybeYouKnowVOs[position]
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.maybeyouknow_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int, ignoreHidden: Boolean): Int {
        return maybeYouKnowVOs.size()
    }

    override fun canCreateMarginViaDecorators(position: Int): Boolean {
        return true
    }

    class ViewHolder(
            val view: VerticalPeopleItemView
    ) : RecyclerView.ViewHolder(view)
}