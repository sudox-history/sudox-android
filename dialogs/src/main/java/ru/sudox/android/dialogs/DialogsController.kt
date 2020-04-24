package ru.sudox.android.dialogs

import ru.sudox.android.core.CoreController
import ru.sudox.android.core.controllers.tabs.TabsRootController
import ru.sudox.android.dialogs.vos.DialogsAppBarVO

class DialogsController : TabsRootController() {

    init {
        appBarVO = DialogsAppBarVO()
    }

    override fun getControllersCount(): Int {
        return 2
    }

    override fun getControllerTitle(position: Int): String {
        return activity!!.getString(if (position == 0) {
            R.string.chats
        } else {
            R.string.talks
        })
    }

    override fun createController(position: Int): CoreController {
        return if (position == 0) {
            ChatsTabController()
        } else {
            TalksTabFragment()
        }
    }
}