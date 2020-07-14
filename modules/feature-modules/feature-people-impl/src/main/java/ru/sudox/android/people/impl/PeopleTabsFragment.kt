package ru.sudox.android.people.impl

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.core.ui.viewpager.ViewPagerFragment
import ru.sudox.android.people.impl.activity.ActivityFragment
import ru.sudox.android.people.impl.people.PeopleFragment

/**
 * Фрагмент-контейнер модуля функционала экрана People.
 */
@AndroidEntryPoint
class PeopleTabsFragment : ViewPagerFragment(R.layout.fragment_people_tabs, R.id.peopleViewPager, R.id.peopleTabLayout) {

    override fun getFragment(position: Int): Fragment = if (position == 0) {
        ActivityFragment()
    } else {
        PeopleFragment()
    }

    override fun getFragmentTitleId(position: Int): Int = if (position == 0) {
        R.string.activity_tab_title
    } else {
        R.string.people_tab_title
    }

    override fun getFragmentsCount(): Int = 2
}