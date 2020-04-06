package ru.sudox.android.people.activitytab.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.vos.ViewListHeaderVO
import ru.sudox.android.people.activitytab.R
import ru.sudox.android.people.activitytab.vos.headers.MomentsHeaderVO
import ru.sudox.android.people.activitytab.vos.headers.NewsHeaderVO
import ru.sudox.android.moments.adapters.MomentsAdapter
import ru.sudox.android.moments.createMomentsRecyclerView
import ru.sudox.android.news.callbacks.NewsSortingCallback
import ru.sudox.android.news.views.NewsItemView
import ru.sudox.android.news.vos.NewsVO

const val MOMENTS_HEADER_TYPE = 0
const val NEWS_HEADER_TYPE = 1

const val MOMENTS_ITEM_VIEW_TYPE = 0
const val NEWS_ITEM_VIEW_TYPE = 1

/**
 * Адаптер для экрана Activity
 */
class ActivityTabAdapter : ViewListAdapter<RecyclerView.ViewHolder>() {

    override var headersVOs: Array<ViewListHeaderVO>? = arrayOf(
            MomentsHeaderVO(),
            NewsHeaderVO()
    )

    var viewPool = RecyclerView.RecycledViewPool()
    val newsVOs = SortedList<NewsVO>(NewsVO::class.java, NewsSortingCallback(this, NEWS_HEADER_TYPE))
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
            NewsViewHolder(NewsItemView(parent.context))
        }
    }

    override fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsViewHolder) {
            holder.view.vo = newsVOs[recalculatePositionRelativeHeader(position)]
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is MomentsAdapter.ViewHolder) {
            holder.view.vo = null
        }
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
        } else if (position == 2) {
            headersVOs!![NEWS_HEADER_TYPE]
        } else {
            null
        }
    }

    override fun getPositionForNewHeader(type: Int): Int {
        // Всегда запрашивается только блок постов.
        return 2
    }

    override fun getItemMargin(position: Int): Int {
        return if (position > 2) {
            viewList!!.context.resources.getDimensionPixelSize(R.dimen.activitytabadapter_margin_between_news)
        } else {
            0
        }
    }

    override fun getItemsCountAfterHeader(type: Int): Int {
        return if (type != MOMENTS_HEADER_TYPE) {
            newsVOs.size()
        } else {
            1
        }
    }

    override fun getHeaderTypeByItemType(itemType: Int): Int {
        return if (itemType == MOMENTS_ITEM_VIEW_TYPE) {
            MOMENTS_HEADER_TYPE
        } else {
            NEWS_HEADER_TYPE
        }
    }

    override fun getHeadersCount(): Int {
        if (newsVOs.size() > 0) {
            return 2
        }

        // Блок историй отображается всегда
        return 1
    }

    class MomentsViewHolder(
            val view: ViewList
    ) : RecyclerView.ViewHolder(view)

    class NewsViewHolder(
            val view: NewsItemView
    ) : RecyclerView.ViewHolder(view)
}