package com.sudox.messenger.android.activity.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.vos.ViewListHeaderVO
import com.sudox.messenger.android.activity.vos.headers.MomentsHeaderVO
import com.sudox.messenger.android.activity.vos.headers.NewsHeaderVO
import com.sudox.messenger.android.moments.adapters.MomentsAdapter
import com.sudox.messenger.android.moments.createMomentsRecyclerView

const val MOMENTS_HEADER_TYPE = 0
const val NEWS_HEADER_TYPE = 1

const val MOMENTS_ITEM_VIEW_TYPE = 0
const val NEWS_ITEM_VIEW_TYPE = 1

class ActivityTabAdapter : ViewListAdapter<RecyclerView.ViewHolder>() {

    override var headersVOs: Array<ViewListHeaderVO>? = arrayOf(
            MomentsHeaderVO(),
            NewsHeaderVO()
    )

    var viewPool = RecyclerView.RecycledViewPool()
    val momentsAdapter = MomentsAdapter()

    override var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                setRecycledViewPool(viewPool)
                setItemViewCacheSize(20)
                setHasFixedSize(true)
            }
        }

    override fun createItemHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MOMENTS_ITEM_VIEW_TYPE) {
            MomentsViewHolder(createMomentsRecyclerView(parent.context).also { list ->
                list.setRecycledViewPool(viewPool)
                list.adapter = momentsAdapter.apply {
                    viewList = list
                }
            })
        } else {
            TODO()
        }
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemType(position: Int): Int {
        return if (position == 1) {
            MOMENTS_ITEM_VIEW_TYPE
        } else {
            NEWS_ITEM_VIEW_TYPE
        }
    }

    override fun getHeaderByPosition(position: Int): ViewListHeaderVO? {
        return if (position == 0) {
            headersVOs!![MOMENTS_HEADER_TYPE]
        } else if  (position == 2) {
            headersVOs!![NEWS_HEADER_TYPE]
        } else {
            null
        }
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return if (type == MOMENTS_HEADER_TYPE) {
            1
        } else {
            0
        }
    }

    override fun getItemMargin(position: Int): Int {
        return 0
    }

    override fun getHeadersCount(): Int {
        return 2
    }

    class MomentsViewHolder(
            val view: ViewList
    ) : RecyclerView.ViewHolder(view)
}