package com.sudox.messenger.android.friends

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.friends.callbacks.FriendsSortingCallback
import com.sudox.messenger.android.friends.views.FriendItemView
import com.sudox.messenger.android.friends.vos.FriendVO
import com.sudox.messenger.android.friends.vos.IS_NOT_REQUEST_TIME
import com.sudox.messenger.android.friends.vos.SEEN_TIME_ONLINE

const val FRIEND_REQUEST_ITEM_TYPE = 0
const val MAYBE_YOU_KNOW_ITEM_TYPE = 1
const val ONLINE_FRIEND_ITEM_TYPE = 2
const val OFFLINE_FRIEND_ITEM_TYPE = 3

class FriendsAdapter(
       private val viewList: ViewList
) : ViewListAdapter<FriendsAdapter.ViewHolder>(viewList) {

    val onlineVO = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, ONLINE_FRIEND_ITEM_TYPE))
    val offlineVO = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, OFFLINE_FRIEND_ITEM_TYPE))
    val maybeYouKnowVO = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, MAYBE_YOU_KNOW_ITEM_TYPE))
    val requestsVO = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, FRIEND_REQUEST_ITEM_TYPE))
    var acceptRequestCallback: ((FriendVO) -> (Unit))? = null
    var rejectRequestCallback: ((FriendVO) -> (Unit))? = null

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FriendItemView(parent.context))
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is ViewHolder) {
            return
        }

        val elementPosition = recalculatePositionRelativeHeader(position)
        val vo = if (holder.itemViewType == FRIEND_REQUEST_ITEM_TYPE) {
            requestsVO[elementPosition]
        } else if (holder.itemViewType == MAYBE_YOU_KNOW_ITEM_TYPE) {
            maybeYouKnowVO[elementPosition]
        } else if (holder.itemViewType == ONLINE_FRIEND_ITEM_TYPE) {
            onlineVO[elementPosition]
        } else if (holder.itemViewType == OFFLINE_FRIEND_ITEM_TYPE) {
            offlineVO[elementPosition]
        } else {
            null
        } ?: return

        holder.view.setUserName(vo.name)
        holder.view.setUserPhoto(vo.photo)
        holder.view.toggleAcceptAndRejectButtons(holder.itemViewType == FRIEND_REQUEST_ITEM_TYPE)

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

    override fun getPositionForNewHeader(type: Int, itemCount: Int): Int {
        if (type == FRIEND_REQUEST_ITEM_TYPE) {
            return 0
        }

        var position = 0

        if (requestsVO.size() > 0) {
            position += requestsVO.size() + 1
        }

        if (type == MAYBE_YOU_KNOW_ITEM_TYPE) {
            return position
        }

        if (maybeYouKnowVO.size() > 0) {
            position += 2
        }

        if (type == ONLINE_FRIEND_ITEM_TYPE) {
            return position
        }

        if (onlineVO.size() > 0) {
            position += onlineVO.size() + 1
        }

        return position
    }

    override fun getHeaderTextByType(type: Int): String? {
        return if (type == FRIEND_REQUEST_ITEM_TYPE) {
            viewList.context.getString(R.string.friends_requests)
        } else if (type == ONLINE_FRIEND_ITEM_TYPE) {
            viewList.context.getString(R.string.online)
        } else if (type == OFFLINE_FRIEND_ITEM_TYPE) {
            viewList.context.getString(R.string.offline)
        } else if (type == MAYBE_YOU_KNOW_ITEM_TYPE) {
            viewList.context.getString(R.string.maybe_you_know)
        } else {
            null
        }
    }

    override fun getHeaderTextByPosition(position: Int): String? {
        if (position == 0) {
            if (requestsVO.size() > 0) {
                return viewList.context.getString(R.string.friends_requests)
            } else if (maybeYouKnowVO.size() > 0) {
                return viewList.context.getString(R.string.maybe_you_know)
            } else if (onlineVO.size() > 0) {
                return viewList.context.getString(R.string.online)
            } else if (offlineVO.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        var sum = requestsVO.size()

        if (requestsVO.size() > 0) {
            sum++
        }

        if (sum > 0 && sum == position) {
            if (maybeYouKnowVO.size() > 0) {
                return viewList.context.getString(R.string.maybe_you_know)
            } else if (onlineVO.size() > 0) {
                return viewList.context.getString(R.string.online)
            } else if (offlineVO.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        sum += maybeYouKnowVO.size()

        if (maybeYouKnowVO.size() > 0) {
            sum++
        }

        if (sum > 0 && sum == position) {
            if (onlineVO.size() > 0) {
                return viewList.context.getString(R.string.online)
            } else if (offlineVO.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        sum += onlineVO.size()

        if (onlineVO.size() > 0) {
            sum++
        }

        if (sum > 0 && sum == position) {
            if (offlineVO.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        return null
    }

    override fun getHeadersCount(): Int {
        var count = 0

        if (requestsVO.size() > 0) {
            count++
        }

        if (maybeYouKnowVO.size() > 0) {
            count++
        }

        if (onlineVO.size() > 0) {
            count++
        }

        if (offlineVO.size() > 0) {
            count++
        }

        return count
    }

    override fun getItemType(position: Int): Int {
        val itemPosition = recalculatePosition(position)
        var itemsCount = requestsVO.size()

        if (itemPosition < itemsCount) {
            return FRIEND_REQUEST_ITEM_TYPE
        }

        itemsCount += maybeYouKnowVO.size()

        if (itemPosition < itemsCount) {
            return MAYBE_YOU_KNOW_ITEM_TYPE
        }

        itemsCount += onlineVO.size()

        if (itemPosition < itemsCount) {
            return ONLINE_FRIEND_ITEM_TYPE
        }

        itemsCount += offlineVO.size()

        if (itemPosition < itemsCount) {
            return OFFLINE_FRIEND_ITEM_TYPE
        }

        TODO("Empty list ;(")
    }

    override fun getItemMargin(position: Int): Int {
        return viewList.context.resources.getDimensionPixelSize(R.dimen.friends_list_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return if (type == FRIEND_REQUEST_ITEM_TYPE) {
            requestsVO.size()
        } else if (type == MAYBE_YOU_KNOW_ITEM_TYPE) {
            maybeYouKnowVO.size()
        } else if (type == ONLINE_FRIEND_ITEM_TYPE) {
            onlineVO.size()
        } else if (type == OFFLINE_FRIEND_ITEM_TYPE) {
            offlineVO.size()
        } else {
            0
        }
    }

    override fun getItemsCount(): Int {
        return requestsVO.size() + maybeYouKnowVO.size() + onlineVO.size() + offlineVO.size()
    }

    class ViewHolder(val view: FriendItemView) : RecyclerView.ViewHolder(view)
}