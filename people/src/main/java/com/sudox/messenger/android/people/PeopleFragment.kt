package com.sudox.messenger.android.people

import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment
import com.sudox.messenger.android.people.activitytab.ActivityTabFragment
import com.sudox.messenger.android.people.peopletab.PeopleTabFragment

class PeopleFragment : TabsRootFragment() {

    override fun getFragments(): Array<CoreFragment> {
        return arrayOf(ActivityTabFragment(), PeopleTabFragment())
    }
}