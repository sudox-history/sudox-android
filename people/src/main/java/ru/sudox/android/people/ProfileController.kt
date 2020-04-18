package ru.sudox.android.people

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.auth.ui.vos.AuthRequestBottomSheetVO
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.design.bottomsheet.createBottomSheetDialog

class ProfileController : ScrollableController() {

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return View(activity)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        createBottomSheetDialog(activity!!, AuthRequestBottomSheetVO("Mi 8"))
                .show()
    }
}
