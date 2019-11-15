package com.sudox.messenger.android.auth.country

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.sortedList.decorations.StickyLettersDecoration
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import kotlinx.android.synthetic.main.fragment_auth_country.authCountryList

class AuthCountryFragment : CoreFragment(false) {

    private var coreActivity: CoreActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        coreActivity = activity as CoreActivity
        coreActivity!!.getScreenManager().setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        coreActivity!!.getApplicationBarManager().let {
            it.toggleButtonBack(true)
            it.toggleButtonNext(false)
            it.setTitleText(R.string.countries)
        }

        return inflater.inflate(R.layout.fragment_auth_country, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val provider = coreActivity!!.getLoader().getCountriesProvider()

        authCountryList.apply {
            setLettersProvider(provider.getLettersProvider())

            adapter = AuthCountryAdapter(context!!, provider.getLoadedCountries())
            layoutManager = LinearLayoutManager(context)
            layoutAnimation = null
            itemAnimator = null
        }
    }
}