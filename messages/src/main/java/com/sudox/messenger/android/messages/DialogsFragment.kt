package com.sudox.messenger.android.messages

import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.tabs.TabsRootFragment
import com.sudox.messenger.android.messages.vos.DialogsAppBarVO

class DialogsFragment : TabsRootFragment() {

    init {
        appBarVO = DialogsAppBarVO()
    }

    override fun getFragments(): Array<CoreFragment> {
        return arrayOf(MessagesTabFragment(), TalksTabFragment())
    }
}