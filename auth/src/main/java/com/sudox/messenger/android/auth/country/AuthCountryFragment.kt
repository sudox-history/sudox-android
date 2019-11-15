package com.sudox.messenger.android.auth.country

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.sortedList.SortedListView
import com.sudox.messenger.android.auth.R
import com.sudox.messenger.android.core.CoreActivity

class AuthCountryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val coreActivity = activity as CoreActivity
        val provider = coreActivity.getLoader().getCountriesProvider()

        coreActivity.getScreenManager().setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        coreActivity.getApplicationBarManager().let {
            it.toggleButtonBack(true)
            it.toggleButtonNext(false)
            it.setTitleText(R.string.countries)
        }

        return SortedListView(context!!).apply {
            setLettersProvider(provider.getLettersProvider())

            adapter = AuthCountryAdapter(context!!, provider.getLoadedCountries())
            layoutManager = LinearLayoutManager(context)
            lettersMargin = resources.getDimensionPixelSize(R.dimen.countryitemview_letters_margin)
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}