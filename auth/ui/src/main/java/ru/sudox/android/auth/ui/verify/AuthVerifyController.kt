package ru.sudox.android.auth.ui.verify

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.android.core.managers.MAIN_ROOT_TAG

class AuthVerifyController : ScrollableController() {

    private var viewModel: AuthVerifyViewModel? = null

    init {
        appBarVO = AuthVerifyAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthVerifyLayout(activity!!)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        viewModel = getViewModel()
        viewModel!!.successLiveData.observe(this, Observer {
            navigationManager!!.showRoot(MAIN_ROOT_TAG)
        })
    }
}