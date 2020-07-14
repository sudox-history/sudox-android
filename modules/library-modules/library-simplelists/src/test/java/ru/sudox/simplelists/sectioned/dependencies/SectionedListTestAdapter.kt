package ru.sudox.simplelists.sectioned.dependencies

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.dependencies.TestListHolder
import ru.sudox.simplelists.loadable.dependencies.LoadableTestLoaderHolder
import ru.sudox.simplelists.sectioned.SectionedListAdapter

/**
 * Тестовый адаптер для списка с секциями.
 */
class SectionedListTestAdapter : SectionedListAdapter() {

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