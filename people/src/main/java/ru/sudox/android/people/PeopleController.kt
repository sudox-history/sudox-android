package ru.sudox.android.people

import ru.sudox.android.core.CoreController
import ru.sudox.android.core.controllers.tabs.TabsRootController
import ru.sudox.android.people.activitytab.ActivityTabController
import ru.sudox.android.people.peopletab.PeopleTabController

class PeopleController : TabsRootController() {

    override fun getControllersCount(): Int {
        return 2
    }

    override fun getControllerTitle(position: Int): String {
        return activity!!.getString(if (position == 0) {
            R.string.activity
        } else {
            R.string.people
        })
    }

    override fun createController(position: Int): CoreController {
        return if (position == 0) {
            ActivityTabController()
        } else {
            PeopleTabController()
        }
    }
}