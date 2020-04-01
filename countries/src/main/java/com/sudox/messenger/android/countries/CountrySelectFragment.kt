package com.sudox.messenger.android.countries

import android.content.Intent
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.core.fragments.ViewListFragment

const val COUNTRY_EXTRA_NAME = "country"

class CountrySelectFragment : ViewListFragment<CountrySelectAdapter>() {

    init {
        appBarVO = CountrySelectAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): CountrySelectAdapter {
        return CountrySelectAdapter(context!!) {
            navigationManager!!.popBackstack()

            targetFragment!!.onActivityResult(0, 0, Intent().apply {
                putExtra(COUNTRY_EXTRA_NAME, it)
            })
        }
    }
}