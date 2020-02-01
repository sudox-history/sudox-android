package com.sudox.messenger.android.friends

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.lists.sectionedList.SectionedListAdapter
import com.sudox.messenger.android.friends.callbacks.FriendsRequestsCallback
import com.sudox.messenger.android.friends.views.FriendItemView
import com.sudox.messenger.android.friends.vos.FriendVO
import com.sudox.messenger.android.friends.vos.IS_NOT_REQUEST_TIME
import com.sudox.messenger.android.friends.vos.SEEN_TIME_ONLINE

const val FRIENDS_REQUESTS_SECTION = 0

class FriendsAdapter(
        val context: Context
) : SectionedListAdapter<FriendsAdapter.ViewHolder>() {

    val requests = SortedList<FriendVO>(FriendVO::class.java, FriendsRequestsCallback(this))
    var acceptRequestCallback: ((FriendVO) -> (Unit))? = null
    var rejectRequestCallback: ((FriendVO) -> (Unit))? = null

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FriendItemView(context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vo = if (isRequestsSection(position)) {
            requests[position]
        } else {
            null
        }!!

        holder.view.setUserName(vo.name)
        holder.view.setUserPhoto(vo.photo)
        holder.view.toggleAcceptAndRejectButtons(vo.requestTime != IS_NOT_REQUEST_TIME)

        if (vo.requestTime != IS_NOT_REQUEST_TIME) {
            holder.view.acceptImageButton!!.setOnClickListener { acceptRequestCallback?.invoke(vo) }
            holder.view.rejectImageButton!!.setOnClickListener { rejectRequestCallback?.invoke(vo) }
        } else {
            holder.view.acceptImageButton!!.setOnClickListener(null)
            holder.view.rejectImageButton!!.setOnClickListener(null)
        }

        if (vo.seenTime == SEEN_TIME_ONLINE) {
            holder.view.setUserOnline()
        } else {
            holder.view.setUserOffline(vo.seenTime)
        }
    }

    override fun getSectionName(position: Int): String? {
        if (requests.size() > 0 && position == 0) {
            return context.resources.getString(R.string.friends_requests)
        }

        return null
    }

    override fun getSectionItemsMargin(position: Int): Int {
        if (isRequestsSection(position)) {
            return context.resources.getDimensionPixelSize(R.dimen.friends_requests_margin)
        }

        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (isRequestsSection(position)) {
            FRIENDS_REQUESTS_SECTION
        } else {
            1
        }
    }

    override fun getItemCount(): Int {
        return requests.size()
    }

    private fun isRequestsSection(position: Int): Boolean {
        return requests.size() > 0 && position >= 0 && position < requests.size()
    }

    class ViewHolder(val view: FriendItemView) : RecyclerView.ViewHolder(view)
}