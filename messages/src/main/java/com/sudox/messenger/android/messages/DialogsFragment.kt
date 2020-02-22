package com.sudox.messenger.android.messages

import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment

class DialogsFragment : TabsRootFragment() {

    override fun getFragments(): Array<CoreFragment> {
        return arrayOf(MessagesTabFragment(), TalksTabFragment())
    }
}