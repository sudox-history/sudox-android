package ru.sudox.android.dialogs.impl

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.core.ui.viewpager.ViewPagerFragment
import ru.sudox.android.dialogs.impl.chats.ChatsFragment
import ru.sudox.android.dialogs.impl.talks.TalksFragment

/**
 * Фрагмент-контейнер табов для модуля диалогов.
 */
@AndroidEntryPoint
class DialogsTabsFragment : ViewPagerFragment(
    R.layout.fragment_dialogs_tabs,
    R.id.dialogsViewPager,
    R.id.dialogsTabLayout
) {

    override fun getFragment(position: Int): Fragment = if (position == 0) {
        ChatsFragment()
    } else {
        TalksFragment()
    }

    override fun getFragmentTitleId(position: Int): Int = if (position == 0) {
        R.string.chats_tab_title
    } else {
        R.string.talks_tab_title
    }

    override fun getFragmentsCount(): Int = 2
}