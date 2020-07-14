package ru.sudox.android.people.impl.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.lists.SectionedScreenListAdapter
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.MY_STORY_SECTION_ORDER
import ru.sudox.android.people.impl.activity.holders.MyStoryViewHolder
import ru.sudox.android.people.impl.activity.holders.OtherStoryViewHolder
import ru.sudox.simplelists.BasicListHolder

/**
 * Адаптер для списка историй.
 *
 * @param fragment Связанный фрагмент
 */
class StoriesAdapter(
    context: Context,
    private val fragment: Fragment
) : SectionedScreenListAdapter(context) {

    override fun createOtherViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> {
        val view = inflater.inflate(R.layout.item_story, parent, false)

        return if (viewType == MY_STORY_SECTION_ORDER) {
            MyStoryViewHolder(view, fragment)
        } else {
            OtherStoryViewHolder(view, fragment)
        }
    }
}