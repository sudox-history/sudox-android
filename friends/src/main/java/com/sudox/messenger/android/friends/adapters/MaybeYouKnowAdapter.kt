package com.sudox.messenger.android.friends.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.friends.R
import com.sudox.messenger.android.friends.callbacks.MaybeYouKnowSortingCallback
import com.sudox.messenger.android.friends.views.MaybeYouKnowItemView
import com.sudox.messenger.android.friends.vos.MaybeYouKnowVO

class MaybeYouKnowAdapter(
       private val viewList: ViewList
) : ViewListAdapter<MaybeYouKnowAdapter.ViewHolder>(viewList) {

    var removeUserCallback: ((MaybeYouKnowVO) -> (Unit))? = null
    val maybeYouKnowVOs = SortedList<MaybeYouKnowVO>(MaybeYouKnowVO::class.java, MaybeYouKnowSortingCallback(this)).apply {
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 1024, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 16384, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
        add(MaybeYouKnowVO(1, "Yaroslav", true, 5, viewList.context.getDrawable(R.drawable.drawable_photo_2)!!))
    }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MaybeYouKnowItemView(viewList.context))
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        val vo = maybeYouKnowVOs[position]

        holder.view.let {
            it.closeImageButton!!.setOnClickListener { removeUserCallback!!(maybeYouKnowVOs[holder.adapterPosition]) }
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