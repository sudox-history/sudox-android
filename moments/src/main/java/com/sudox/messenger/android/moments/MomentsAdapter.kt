package com.sudox.messenger.android.moments

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.moments.views.MomentItemView
import com.sudox.messenger.android.moments.vos.MomentVO

class MomentsAdapter(
        val context: Context
) : RecyclerView.Adapter<MomentsAdapter.ViewHolder>() {

    val moments = SortedList<MomentVO>(MomentVO::class.java, MomentsCallback(this))
    var showMomentCallback: ((MomentVO) -> (Unit))? = null
    var addMomentCallback: (() -> (Unit))? = null

    private var publisherPhoto: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MomentItemView(context)
        val holder = ViewHolder(view)

        view.setOnClickListener {
            if (holder.adapterPosition == 0) {
                addMomentCallback!!()
            } else {
                showMomentCallback!!(moments[holder.adapterPosition - 1])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.let {
            if (position == 0) {
                it.setPublisherPhoto(publisherPhoto)
                it.setCreatedByMe(true)
                it.setViewed(true)
            } else {
                val moment = moments[position - 1]

                it.setCreatedByMe(false)
                it.setUserName(moment.publisherName)
                it.setPublisherPhoto(moment.publisherPhoto)
                it.setViewed(moment.isStartViewed)
            }
        }
    }

    override fun getItemCount(): Int {
        return moments.size() + 1
    }

    fun setUserPhoto(userPhoto: Drawable) {
        this.publisherPhoto = userPhoto
        this.notifyItemChanged(0)
    }

    class ViewHolder(val view: MomentItemView) : RecyclerView.ViewHolder(view)
}