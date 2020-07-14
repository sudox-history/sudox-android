package ru.sudox.simplelists.dependencies

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ru.sudox.simplelists.BasicListAdapter
import ru.sudox.simplelists.BasicListHolder

/**
 * Тестовый адаптер
 */
class TestListAdapter : BasicListAdapter() {

    override fun createViewHolder(context: Context, inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BasicListHolder<*> {
        return TestListHolder(TextView(context))
    }
}