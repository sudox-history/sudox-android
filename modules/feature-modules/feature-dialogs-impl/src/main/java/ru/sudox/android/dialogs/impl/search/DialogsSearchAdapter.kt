package ru.sudox.android.dialogs.impl.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.lists.SectionedScreenListAdapter
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.search.holders.FoundChatViewHolder
import ru.sudox.simplelists.BasicListHolder

/**
 * Адаптер для списка найденных диалогов.
 *
 * @param context Контекст приложения/активности
 * @param fragment Связанный фрагмент.
 */
class DialogsSearchAdapter(
    context: Context,
    private val fragment: Fragment
) : SectionedScreenListAdapter(context) {

    override fun createOtherViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> = if (viewType == FOUND_CHAT_SECTION_ORDER) {
        FoundChatViewHolder(
            inflater.inflate(
                R.layout.item_found_chat,
                parent,
                false
            ), fragment
        )
    } else {
        TODO()
    }
}