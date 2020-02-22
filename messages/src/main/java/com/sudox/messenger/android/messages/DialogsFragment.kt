package com.sudox.messenger.android.messages

import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment

class DialogsFragment : TabsRootFragment() {

    override fun getFragments(): Array<TabsChildFragment> {
        return arrayOf(MessagesTabFragment(), TalksTabFragment())
    }
}