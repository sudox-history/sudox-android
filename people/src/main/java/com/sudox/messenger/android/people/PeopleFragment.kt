package com.sudox.messenger.android.people

import com.sudox.messenger.android.activity.ActivityTabFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment
import com.sudox.messenger.android.people.peopletab.PeopleTabFragment

class PeopleFragment : TabsRootFragment() {

    override fun getFragments(): Array<TabsChildFragment> {
        return arrayOf(ActivityTabFragment(), PeopleTabFragment())
    }
}