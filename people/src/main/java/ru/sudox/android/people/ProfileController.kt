package ru.sudox.android.people

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.CoreController

class ProfileController : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return View(activity)
    }

}
