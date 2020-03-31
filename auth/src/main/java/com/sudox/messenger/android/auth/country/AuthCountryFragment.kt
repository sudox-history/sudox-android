package com.sudox.messenger.android.auth.country

import android.content.Intent
import android.content.pm.ActivityInfo
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.design.countriesProvider.getCountries
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.core.fragments.ViewListFragment

internal const val COUNTRY_EXTRA_NAME = "country"

class AuthCountryFragment : ViewListFragment<AuthCountryAdapter>() {

    init {
        appBarVO = AuthCountryAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): AuthCountryAdapter? {
        return AuthCountryAdapter(context!!, getCountries(context!!)).apply {
            clickCallback = ::onCountryClicked
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            screenManager!!.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }

        super.onHiddenChanged(hidden)
    }

    private fun onCountryClicked(country: Country) {
        navigationManager!!.popBackstack()
        targetFragment!!.onActivityResult(0, 0, Intent().apply {
            putExtra(COUNTRY_EXTRA_NAME, country)
        })
    }
}
