package com.sudox.messenger.android.activity

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.sectionedList.SectionedListAdapter
import com.sudox.messenger.android.moments.MomentVO
import com.sudox.messenger.android.moments.createMomentsRecyclerView

private const val MOMENTS_SECTION = 0
private const val FRIENDS_NEWS_SECTION = 1

class ActivityAdapter(
        val context: Context,
        val moments: ArrayList<MomentVO>
) : SectionedListAdapter<ActivityAdapter.BaseViewHolder>() {

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == MOMENTS_SECTION) {
            BaseViewHolder(createMomentsRecyclerView(context, moments))
        } else {
            BaseViewHolder(TextView(context).apply {
                text = "Content of new section"
            })
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            MOMENTS_SECTION
        } else {
            FRIENDS_NEWS_SECTION
        }
    }

    override fun getSectionName(position: Int): String? {
        return if (position == 0) {
            context.getString(R.string.moments)
        } else if (position == 1) {
            context.getString(R.string.friends_news)
        } else {
            null
        }
    }

    override fun getSectionItemsMargin(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return 2
    }

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}