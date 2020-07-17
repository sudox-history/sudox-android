package ru.sudox.android.dialogs.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dialogs_tabs.*
import ru.sudox.android.core.ui.setChildEnterAnimations
import ru.sudox.android.core.ui.viewpager.ViewPagerFragment
import ru.sudox.android.dialogs.impl.chats.ChatsFragment
import ru.sudox.android.dialogs.impl.search.DialogsSearchFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogsTabsToolbar.setOnMenuItemClickListener {
            parentFragmentManager.commit {
                setChildEnterAnimations()
                replace(R.id.dialogsFlowContainer, DialogsSearchFragment())
                addToBackStack(null)
            }

            true
        }
    }

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