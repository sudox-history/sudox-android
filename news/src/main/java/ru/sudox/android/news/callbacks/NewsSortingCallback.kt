package ru.sudox.android.news.callbacks

import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListCallback
import ru.sudox.android.news.vos.NewsVO

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