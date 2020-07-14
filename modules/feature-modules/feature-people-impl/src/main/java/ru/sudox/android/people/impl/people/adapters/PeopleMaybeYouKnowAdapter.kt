package ru.sudox.android.people.impl.people.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.lists.ScreenListAdapter
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.holders.PeopleMaybeYouKnowHolder
import ru.sudox.android.people.impl.people.viewobjects.PeopleMaybeYouKnowViewObject
import ru.sudox.simplelists.BasicListHolder

/**
 * Адаптер для списка возможно знакомых людей.
 *
 * @param fragment Связанный фрагмент
 * @param onRemoved Функция, вызываемая при удалении записи.
 */
class PeopleMaybeYouKnowAdapter(
    private val fragment: Fragment,
    private val onRemoved: (PeopleMaybeYouKnowViewObject) -> (Unit)
) : ScreenListAdapter() {

    override fun createItemViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> =
        PeopleMaybeYouKnowHolder(inflater.inflate(R.layout.item_people_maybe_you_know, parent, false), fragment, onRemoved)
}