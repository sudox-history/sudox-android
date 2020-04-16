package ru.sudox.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.android.people.common.views.VerticalPeopleItemView
import ru.sudox.android.people.peopletab.R
import ru.sudox.android.people.peopletab.callbacks.MaybeYouKnowSortingCallback
import ru.sudox.android.people.peopletab.vos.MaybeYouKnowVO

const val MAYBE_YOU_KNOW_ITEM_VIEW_TYPE = 4

/**
 * Адаптер для блока "Maybe you know".
 */
class MaybeYouKnowAdapter(
        val glide: GlideRequests
) : ViewListAdapter<MaybeYouKnowAdapter.ViewHolder>() {

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
        holder.view.setVO(maybeYouKnowVOs[position], glide)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is ViewHolder) {
            holder.view.setVO(null, glide)
        }
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