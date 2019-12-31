package com.sudox.messenger.android.auth.country

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.design.lists.sortedList.SortedListView
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity

internal const val COUNTRY_EXTRA_NAME = "country"

class AuthCountryFragment : Fragment() {

    private var coreActivity: CoreActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        coreActivity = activity as CoreActivity
        coreActivity!!.getScreenManager().setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        coreActivity!!.getApplicationBarManager().let {
            it.toggleButtonBack(true)
            it.toggleButtonNext(false)
            it.setTitleText(R.string.countries)
        }

        return inflater.inflate(R.layout.fragment_auth_country, container, false).apply {
            val provider = coreActivity!!.getLoader().getCountriesProvider()
            val listView = this as SortedListView

            listView.setLettersProvider(provider.getLettersProvider())
            listView.layoutManager = LinearLayoutManager(context)
            listView.adapter = AuthCountryAdapter(context, provider.getLoadedCountries()).apply {
                clickCallback = ::onCountryClicked
            }
        }
    }

    private fun onCountryClicked(country: Country) {
        coreActivity!!.getNavigationManager().popBackstack()
        targetFragment!!.onActivityResult(0, 0, Intent().apply {
            putExtra(COUNTRY_EXTRA_NAME, country)
        })
    }
}
