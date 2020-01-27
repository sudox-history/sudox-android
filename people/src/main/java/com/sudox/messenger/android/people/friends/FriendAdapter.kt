package com.sudox.messenger.android.people.friends

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.sectionedList.SectionedListAdapter
import com.sudox.messenger.android.people.R

private const val REQUESTS_VIEW = 0
private const val MAYBE_YOU_KNOW_VIEW = 1
private const val ONLINE_VIEW = 2
private const val OFFLINE_VIEW = 3

class FriendAdapter(val context: Context) : SectionedListAdapter<FriendAdapter.ViewHolder>() {

    private var requestsSize = 3
    private var maybeYouKnowSize = 1
    private var onlineSize = 1
    private var offlineSize = 1

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        return requestsSize +
                maybeYouKnowSize +
                onlineSize +
                offlineSize
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var sum = requestsSize
        if (position < sum) {
            val item = holder.view as FriendRequestsItemView
            if (position == 0) {
                item.apply {
                    setNameText("Anton")
                    setAvatar(context.getDrawable(R.drawable.drawable_photo_1))
                    setOnlineStatus(true)
                }
            }
            else {
                item.apply {
                    setNameText("Yaroslav")
                    setAvatar(context.getDrawable(R.drawable.drawable_photo_2))
                    setOnlineStatus(false)
                }
            }
        }

        sum += maybeYouKnowSize
        if (position < sum) {
            // todo: realize maybe you know view
        }

        sum += onlineSize
        if (position < sum) {
            // todo: realize online view
        }
        else {
            // todo realize offline view
        }
    }

    override fun getSectionName(position: Int): String? {
        var sum = 0
        if (position == sum) {
            return "Friend requests"
        }

        sum += requestsSize
        if (position < sum) {
            return null
        }
        if (position == sum) {
            return "Maybe you know"
        }

        sum += maybeYouKnowSize
        if (position < sum) {
            return null
        }
        if (position == sum) {
            return "Online" // todo: add string res definition
        }

        sum += onlineSize
        if (position < sum) {
            return null
        }
        if (position == sum) {
            return "Offline"
        }
        else {
            return null
        }
    }

    override fun getSectionItemsMargin(position: Int): Int {
        var sum = requestsSize
        if (position < sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.another_friend_request_margin)
        }
        if (position == sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.last_friend_request_margin)
        }

        sum += requestsSize
        if (position < sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.another_maybe_you_know_margin)
        }
        if (position == sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.last_maybe_you_know_margin)
        }

        sum += maybeYouKnowSize
        if (position < sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.another_online_margin)
        }
        if (position == sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.last_online_margin)
        }

        sum += onlineSize
        if (position < sum - 1) {
            return context.resources.getDimensionPixelSize(R.dimen.another_offline_margin)
        }
        else {
            return context.resources.getDimensionPixelSize(R.dimen.last_offline_margin)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var sum = requestsSize
        if (position < sum) {
            return REQUESTS_VIEW
        }

        sum += maybeYouKnowSize
        if (position < sum) {
            return MAYBE_YOU_KNOW_VIEW
        }

        sum += onlineSize
        if (position < sum) {
            return ONLINE_VIEW
        }
        else {
            return OFFLINE_VIEW
        }
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  =
            if (viewType == REQUESTS_VIEW)
                ViewHolder(FriendRequestsItemView(context))
            else if (viewType == MAYBE_YOU_KNOW_VIEW)
                ViewHolder(FriendRequestsItemView(context)) // todo: fix it
            else if (viewType == ONLINE_VIEW)
                ViewHolder(FriendRequestsItemView(context)) // todo: fix it
            else
                ViewHolder(FriendRequestsItemView(context)) // todo: fix it
}