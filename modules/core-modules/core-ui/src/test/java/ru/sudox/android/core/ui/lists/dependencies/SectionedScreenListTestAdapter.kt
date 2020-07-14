package ru.sudox.android.core.ui.lists.dependencies

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ru.sudox.android.core.ui.lists.SectionedScreenListAdapter
import ru.sudox.simplelists.BasicListHolder

/**
 * Провайдер для тестируемого списка.
 */
class SectionedScreenListTestAdapter(context: Context) : SectionedScreenListAdapter(context) {

    override fun createOtherViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> = SectionedScreenListTestItemHolder(TextView(context))
}