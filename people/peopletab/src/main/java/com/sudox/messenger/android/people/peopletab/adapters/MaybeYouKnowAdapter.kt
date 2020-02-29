package com.sudox.messenger.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.common.views.VerticalPeopleItemView
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.callbacks.MaybeYouKnowSortingCallback
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

const val MAYBE_YOU_KNOW_ITEM_VIEW_TYPE = 4

/**
 * Адаптер для блока "Maybe you know".
 */
class MaybeYouKnowAdapter : ViewListAdapter<MaybeYouKnowAdapter.ViewHolder>() {

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

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

    override fun getItemType(position: Int): Int {
        return MAYBE_YOU_KNOW_ITEM_VIEW_TYPE
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return maybeYouKnowVOs.size()
    }

    override fun canCreateMarginViaDecorators(): Boolean {
        return true
    }

    class ViewHolder(
            val view: VerticalPeopleItemView
    ) : RecyclerView.ViewHolder(view)
}