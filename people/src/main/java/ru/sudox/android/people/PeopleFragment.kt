package ru.sudox.android.people

import ru.sudox.android.core.CoreFragment
import ru.sudox.android.core.tabs.TabsRootFragment
import ru.sudox.android.people.activitytab.ActivityTabFragment
import ru.sudox.android.people.peopletab.PeopleTabFragment

class PeopleFragment : TabsRootFragment() {

    override fun getFragments(): Array<CoreFragment> {
        return arrayOf(ActivityTabFragment(), PeopleTabFragment())
    }
}