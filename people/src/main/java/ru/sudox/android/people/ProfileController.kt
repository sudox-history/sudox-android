package ru.sudox.android.people

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.controllers.ScrollableController

class ProfileController : ScrollableController() {

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return View(activity!!)
    }
}
