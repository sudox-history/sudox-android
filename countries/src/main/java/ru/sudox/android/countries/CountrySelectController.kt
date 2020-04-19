package ru.sudox.android.countries

import android.content.Intent
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.design.viewlist.ViewList

const val COUNTRY_EXTRA_NAME = "country"
const val COUNTRY_CHANGE_REQUEST_CODE = 1

class CountrySelectController : ViewListController<CountrySelectAdapter>() {

    init {
        appBarVO = CountrySelectAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): CountrySelectAdapter {
        return CountrySelectAdapter(activity!!) {
            navigationManager!!.popBackstack()
            targetController!!.onActivityResult(COUNTRY_CHANGE_REQUEST_CODE, 0, Intent().apply {
                putExtra(COUNTRY_EXTRA_NAME, it)
            })
        }
    }

    override fun onAppBarClicked(tag: Int) {
        super.onAppBarClicked(tag)
    }
}