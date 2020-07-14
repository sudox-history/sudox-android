package ru.sudox.simplelists.loadable.dependencies

import android.widget.ProgressBar
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

/**
 * Тестовый Holder для загрузчика.
 *
 * @param view View загрузчика.
 */
class LoadableTestLoaderHolder(
    private val view: ProgressBar
) : BasicListHolder<Nothing>(view) {

    override fun bind(item: BasicListItem<Nothing>, changePayload: List<Any>?) {
        view.isIndeterminate = true
    }

    override fun cancelAnimations() {
        view.isIndeterminate = false
    }
}