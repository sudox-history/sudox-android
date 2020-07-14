package ru.sudox.simplelists.loadable.dependencies

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.dependencies.TestListHolder
import ru.sudox.simplelists.loadable.LoadableListAdapter

/**
 * Тестовый адаптер для списка с поддержкой загрузки.
 */
class LoadableTestAdapter : LoadableListAdapter() {

    override fun createItemViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> {
        return TestListHolder(TextView(context))
    }

    override fun createLoaderViewHolder(context: Context, inflater: LayoutInflater, parent: ViewGroup): BasicListHolder<*> {
        return LoadableTestLoaderHolder(ProgressBar(context))
    }
}