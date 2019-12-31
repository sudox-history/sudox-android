package com.sudox.messenger.android.moments

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MomentsAdapter(
        val moments: ArrayList<MomentVO>,
        val context: Context
) : RecyclerView.Adapter<MomentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MomentItemView(context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moment = moments[position]

        holder.view.let {
            it.setUserPhoto(moment.userPhoto)
            it.setUserName(moment.userName)
            it.setCreatedByMe(moment.isCreatedByMe)
            it.setViewed(moment.isViewed)
        }
    }

    override fun getItemCount(): Int {
        return moments.size
    }

    class ViewHolder(val view: MomentItemView) : RecyclerView.ViewHolder(view)
}