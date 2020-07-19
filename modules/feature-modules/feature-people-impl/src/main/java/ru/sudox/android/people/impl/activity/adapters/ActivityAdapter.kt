package ru.sudox.android.people.impl.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.ui.lists.SectionedScreenListAdapter
import ru.sudox.simplelists.helpers.NestedScrollableHost
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.STORIES_SECTION_ORDER
import ru.sudox.android.people.impl.activity.holders.PostViewHolder
import ru.sudox.android.people.impl.activity.holders.StoriesListViewHolder
import ru.sudox.simplelists.BasicListHolder

/**
 * Адаптер для экрана Activity
 *
 * @param context Контекст приложения/активности
 * @param storiesAdapter Адаптер списка историй.
 * @param fragment Связанный фрагмент.
 */
class ActivityAdapter(
    context: Context,
    private val storiesAdapter: StoriesAdapter,
    private val fragment: Fragment
) : SectionedScreenListAdapter(context) {

    private val mediaViewPool = RecyclerView.RecycledViewPool()

    override fun createOtherViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> = if (viewType == STORIES_SECTION_ORDER) {
        val view = inflater.inflate(R.layout.item_stories_list, parent, false) as NestedScrollableHost
        val recyclerView = view.getChildAt(0) as RecyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = storiesAdapter

        StoriesListViewHolder(view)
    } else {
        PostViewHolder(inflater.inflate(R.layout.item_post, parent, false), fragment, mediaViewPool)
    }
}