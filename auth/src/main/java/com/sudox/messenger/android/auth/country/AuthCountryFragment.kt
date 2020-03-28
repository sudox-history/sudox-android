package com.sudox.messenger.android.auth.country

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.design.lists.sortedList.SortedListView
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.livedata.Event

internal const val COUNTRY_EXTRA_NAME = "country"

class AuthCountryFragment : CoreFragment() {

    init {
        appBarVO = AuthCountryAppBarVO()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val coreActivity = activity as CoreActivity

        return inflater.inflate(R.layout.fragment_auth_country, container, false).apply {
            val provider = coreActivity.getLoader().getCountriesProvider()
            val listView = this as SortedListView

            listView.setLettersProvider(provider.getLettersProvider())
            listView.layoutManager = LinearLayoutManager(context)
            listView.adapter = AuthCountryAdapter(context, provider.getLoadedCountries()).apply {
                clickCallback = ::onCountryClicked
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            screenManager!!.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }

        super.onHiddenChanged(hidden)
    }

    private fun onCountryClicked(country: Country) {
        findNavController().let {
            newNavigationManager!!.sendData(it, COUNTRY_EXTRA_NAME, Event(country))
            it.popBackStack()
        }
    }
}
