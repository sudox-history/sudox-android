package com.sudox.messenger.android.friends.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.friends.R
import com.sudox.messenger.android.friends.callbacks.FriendsSortingCallback
import com.sudox.messenger.android.friends.createMaybeYouKnowList
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

    val maybeYouKnowViewList = createMaybeYouKnowList(viewList.context)
    val maybeYouKnowAdapter = maybeYouKnowViewList.adapter as MaybeYouKnowAdapter
    val onlineVOs = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, ONLINE_FRIEND_ITEM_TYPE))
    val offlineVOs = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, OFFLINE_FRIEND_ITEM_TYPE))
    val requestsVOs = SortedList<FriendVO>(FriendVO::class.java, FriendsSortingCallback(this, FRIEND_REQUEST_ITEM_TYPE))
    var acceptRequestCallback: ((FriendVO) -> (Unit))? = null
    var rejectRequestCallback: ((FriendVO) -> (Unit))? = null

    override fun createItemHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(if (viewType == MAYBE_YOU_KNOW_ITEM_TYPE) {
            maybeYouKnowViewList
        } else {
            FriendItemView(parent.context)
        })
    }

    override fun bindItemHolder(holder: ViewHolder, position: Int) {
        if (holder.view is FriendItemView) {
            val elementPosition = recalculatePositionRelativeHeader(position)
            val vo = if (holder.itemViewType == FRIEND_REQUEST_ITEM_TYPE) {
                requestsVOs[elementPosition]
            } else if (holder.itemViewType == ONLINE_FRIEND_ITEM_TYPE) {
                onlineVOs[elementPosition]
            } else if (holder.itemViewType == OFFLINE_FRIEND_ITEM_TYPE) {
                offlineVOs[elementPosition]
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
    }

    override fun getPositionForNewHeader(type: Int): Int {
        if (type == FRIEND_REQUEST_ITEM_TYPE) {
            return 0
        }

        var position = 0

        if (requestsVOs.size() > 0) {
            position += requestsVOs.size() + 1
        }

        if (type == MAYBE_YOU_KNOW_ITEM_TYPE) {
            return position
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            position += 2
        }

        if (type == ONLINE_FRIEND_ITEM_TYPE) {
            return position
        }

        if (onlineVOs.size() > 0) {
            position += onlineVOs.size() + 1
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
            if (requestsVOs.size() > 0) {
                return viewList.context.getString(R.string.friends_requests)
            } else if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return viewList.context.getString(R.string.maybe_you_know)
            } else if (onlineVOs.size() > 0) {
                return viewList.context.getString(R.string.online)
            } else if (offlineVOs.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        var sum = requestsVOs.size()

        if (requestsVOs.size() > 0) {
            sum++
        }

        if (sum > 0 && sum == position) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                return viewList.context.getString(R.string.maybe_you_know)
            } else if (onlineVOs.size() > 0) {
                return viewList.context.getString(R.string.online)
            } else if (offlineVOs.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            sum += 2
        }

        if (sum > 0 && sum == position) {
            if (onlineVOs.size() > 0) {
                return viewList.context.getString(R.string.online)
            } else if (offlineVOs.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        sum += onlineVOs.size()

        if (onlineVOs.size() > 0) {
            sum++
        }

        if (sum > 0 && sum == position) {
            if (offlineVOs.size() > 0) {
                return viewList.context.getString(R.string.offline)
            }
        }

        return null
    }

    override fun getHeadersCount(): Int {
        var count = 0

        if (requestsVOs.size() > 0) {
            count++
        }

        if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            count++
        }

        if (onlineVOs.size() > 0) {
            count++
        }

        if (offlineVOs.size() > 0) {
            count++
        }

        return count
    }

    override fun getItemType(position: Int): Int {
        val itemPosition = recalculatePosition(position)
        var itemsCount = requestsVOs.size()

        if (itemPosition < itemsCount) {
            return FRIEND_REQUEST_ITEM_TYPE
        }

        itemsCount += if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            1
        } else {
            0
        }

        if (itemPosition < itemsCount) {
            return MAYBE_YOU_KNOW_ITEM_TYPE
        }

        itemsCount += onlineVOs.size()

        if (itemPosition < itemsCount) {
            return ONLINE_FRIEND_ITEM_TYPE
        }

        itemsCount += offlineVOs.size()

        if (itemPosition < itemsCount) {
            return OFFLINE_FRIEND_ITEM_TYPE
        }

        TODO("Empty list ;(")
    }

    override fun getItemMargin(position: Int): Int {
        return viewList.context.resources.getDimensionPixelSize(R.dimen.mutual_friends_list_items_margin)
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return if (type == FRIEND_REQUEST_ITEM_TYPE) {
            requestsVOs.size()
        } else if (type == MAYBE_YOU_KNOW_ITEM_TYPE) {
            if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
                1
            } else {
                0
            }
        } else if (type == ONLINE_FRIEND_ITEM_TYPE) {
            onlineVOs.size()
        } else if (type == OFFLINE_FRIEND_ITEM_TYPE) {
            offlineVOs.size()
        } else {
            0
        }
    }

    override fun getItemsCount(): Int {
        return requestsVOs.size() + onlineVOs.size() + offlineVOs.size() + if (maybeYouKnowAdapter.maybeYouKnowVOs.size() > 0) {
            1
        } else {
            0
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}