package com.sudox.messenger.android.activity

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.sectionedList.SectionedListAdapter
import com.sudox.messenger.android.moments.MomentsAdapter
import com.sudox.messenger.android.moments.createMomentsRecyclerView
import com.sudox.messenger.android.news.views.NewsItemView
import com.sudox.messenger.android.news.vos.NewsVO
import java.util.Deque

private const val MOMENTS_SECTION = 0
private const val FRIENDS_NEWS_SECTION = 1

class ActivityAdapter(
        val context: Context,
        recyclerView: RecyclerView
) : SectionedListAdapter<ActivityAdapter.BaseViewHolder>() {

    var viewPool = RecyclerView.RecycledViewPool()
    var momentsRecyclerView = createMomentsRecyclerView(context)
    var momentsAdapter = momentsRecyclerView.adapter as MomentsAdapter
    var news: Deque<NewsVO>? = null
    // TODO: Click callback+

    init {
        recyclerView.setRecycledViewPool(viewPool)
        momentsRecyclerView.setRecycledViewPool(viewPool)
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == MOMENTS_SECTION) {
            BaseViewHolder(momentsRecyclerView)
        } else {
            BaseViewHolder(NewsItemView(context))
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
        return 1 + (news?.size ?: 0)
    }

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}