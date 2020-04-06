package ru.sudox.android.messages

import ru.sudox.android.core.CoreFragment
import ru.sudox.android.core.tabs.TabsRootFragment
import ru.sudox.android.messages.vos.DialogsAppBarVO

class DialogsFragment : TabsRootFragment() {

    init {
        appBarVO = DialogsAppBarVO()
    }

    override fun getFragments(): Array<CoreFragment> {
        return arrayOf(ChatsTabFragment(), TalksTabFragment())
    }
}