package com.sudox.messenger.android.news.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.news.vos.NewsVO

/**
 * Кэллбэк для сортировки постов.
 */
class NewsSortingCallback(
        viewListAdapter: ViewListAdapter<*>,
        headerType: Int = 0
) : ViewListCallback<NewsVO>(viewListAdapter, headerType) {

    override fun compare(first: NewsVO, second: NewsVO): Int {
        return -first.publishTime.compareTo(second.publishTime)
    }
}