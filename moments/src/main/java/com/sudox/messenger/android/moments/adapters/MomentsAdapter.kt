package com.sudox.messenger.android.moments.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.circularupdatableview.CircularUpdatableView
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.moments.R
import com.sudox.messenger.android.moments.callbacks.MomentsSortingCallback
import com.sudox.messenger.android.moments.vos.MomentVO

const val MOMENTS_ITEM_VIEW_TYPE = 0

/**
 * Адаптер для моментов.
 */
class MomentsAdapter : ViewListAdapter<MomentsAdapter.ViewHolder>() {

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

    val momentsVOs = SortedList<MomentVO>(MomentVO::class.java, MomentsSortingCallback(this))
    val addMomentVO: MomentVO? = null

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CircularUpdatableView(parent.context))
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        holder.view.vo = if (position != 0) {
            momentsVOs[position - 1]
        } else {
            addMomentVO
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is ViewHolder) {
            holder.view.vo = null
        }
    }

    override fun getItemType(position: Int): Int {
        return MOMENTS_ITEM_VIEW_TYPE
    }

    override fun getItemMargin(position: Int): Int {
        return viewList!!.context.resources.getDimensionPixelSize(R.dimen.moments_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return momentsVOs.size() + 1
    }

    override fun canCreateMarginViaDecorators(): Boolean {
        return true
    }

    class ViewHolder(
            val view: CircularUpdatableView
    ) : RecyclerView.ViewHolder(view)
}