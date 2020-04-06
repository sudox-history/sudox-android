package ru.sudox.android.moments.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.sudox.design.circularupdatableview.CircularUpdatableView
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.android.moments.R
import ru.sudox.android.moments.callbacks.MomentsSortingCallback
import ru.sudox.android.moments.vos.MomentVO

const val MOMENTS_ITEM_VIEW_TYPE = 2

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

    val momentsVOs = SortedList<MomentVO>(MomentVO::class.java, MomentsSortingCallback(this, 1))
    var addMomentVO: MomentVO? = null

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

    override fun canCreateHeaderOrFooter(): Boolean {
        return false
    }

    override fun canCreateMarginViaDecorators(): Boolean {
        return true
    }

    class ViewHolder(
            val view: CircularUpdatableView
    ) : RecyclerView.ViewHolder(view)
}