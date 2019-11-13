package com.sudox.messenger.android.auth.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.common.supportedCountries
import com.sudox.messenger.android.auth.R
import kotlinx.android.synthetic.main.fragment_auth_country.authCountryList

class AuthCountryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_country, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val countries = supportedCountries.sortedBy {
            it.getName(context!!)
        }

        authCountryList.apply {
            adapter = AuthCountryAdapter(context, countries)
            layoutManager = LinearLayoutManager(context)

            setLettersProvider(AuthCountryLettersProvider(countries))
        }
    }
}