package com.sudox.messenger.android.people.peopletab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.peopletab.R
import com.sudox.messenger.android.people.peopletab.callbacks.MaybeYouKnowSortingCallback
import com.sudox.messenger.android.people.peopletab.views.MaybeYouKnowItemView
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO

class MaybeYouKnowAdapter(
       private val viewList: ViewList
) : ViewListAdapter<MaybeYouKnowAdapter.ViewHolder>(viewList) {

    var userClickCallback: ((MaybeYouKnowVO) -> (Unit))? = null
    var removeUserCallback: ((MaybeYouKnowVO) -> (Unit))? = null
    val maybeYouKnowVOs = SortedList<MaybeYouKnowVO>(MaybeYouKnowVO::class.java, MaybeYouKnowSortingCallback(this))

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MaybeYouKnowItemView(viewList.context))
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        val vo = maybeYouKnowVOs[position]

        holder.view.let {
            it.closeImageButton!!.setOnClickListener { removeUserCallback!!(maybeYouKnowVOs[holder.adapterPosition]) }
            it.setOnClickListener { userClickCallback!!(maybeYouKnowVOs[holder.adapterPosition]) }
            it.setMutualFriendsCount(vo.mutualFriendsCount)
            it.setUserOnline(vo.isOnline)
            it.setUserPhoto(vo.photo)
            it.setUserName(vo.name)
        }
    }

    override fun getItemMargin(position: Int): Int {
        return viewList.context.resources.getDimensionPixelSize(R.dimen.maybe_you_know_list_items_margin)
    }

    override fun canCreateHeaderOrFooter(): Boolean {
        return false
    }

    override fun canCreateMarginViaDecorators(position: Int): Boolean {
        return true
    }

    override fun getItemsCount(): Int {
        return maybeYouKnowVOs.size()
    }

    class ViewHolder(val view: MaybeYouKnowItemView) : RecyclerView.ViewHolder(view)
}