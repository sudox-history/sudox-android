package ru.sudox.android.auth.ui.phone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import ru.sudox.android.auth.views.AuthScreenLayout
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.android.countries.COUNTRY_CHANGE_REQUEST_CODE
import ru.sudox.android.countries.COUNTRY_EXTRA_NAME
import ru.sudox.android.countries.CountrySelectController

class AuthPhoneController : ScrollableController() {

    private val screenVO = AuthPhoneScreenVO()

    init {
        appBarVO = AuthPhoneAppBarVO()
    }

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AuthScreenLayout(container.context).apply {
            vo = screenVO
        }
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)

        if (changeType.isEnter) {
            screenVO.phoneEditText!!.countrySelector.setOnClickListener {
                navigationManager!!.showChild(CountrySelectController().apply {
                    targetController = this@AuthPhoneController
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == COUNTRY_CHANGE_REQUEST_CODE) {
            screenVO.phoneEditText!!.vo = data!!.getParcelableExtra(COUNTRY_EXTRA_NAME)
        }
    }
}