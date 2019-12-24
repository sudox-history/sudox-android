package com.sudox.messenger.android.moments

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.sortedList.decorations.MarginItemDecoration

class MomentAdapter(
        recyclerView: RecyclerView,
        val moments: ArrayList<MomentVO>,
        val context: Context
) : RecyclerView.Adapter<MomentAdapter.ViewHolder>() {

    init {
        val horizontalMargin = context.resources.getDimensionPixelSize(R.dimen.momentadapter_horizontal_margin)
        val verticalMargin = context.resources.getDimensionPixelSize(R.dimen.momentadapter_vertical_margim)

        recyclerView.addItemDecoration(MarginItemDecoration(verticalMargin, horizontalMargin))
    }

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