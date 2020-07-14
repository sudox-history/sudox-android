package ru.sudox.android.auth.impl.fragments.phone

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import ru.sudox.android.auth.impl.R
import ru.sudox.android.auth.impl.fragments.code.AuthCodeFragment
import ru.sudox.android.core.ui.applyInserts
import ru.sudox.android.core.ui.setChildEnterAnimations
import ru.sudox.android.core.ui.setupWithFragmentManager
import ru.sudox.android.core.ui.toolbar.helpers.setupWithScrollView
import ru.sudox.android.countries.api.CountriesFeatureApi
import javax.inject.Inject

/**
 * Фрагмент ввода номера телефона.
 */
@AndroidEntryPoint
class AuthPhoneFragment : Fragment(R.layout.fragment_auth_phone) {

    @Inject
    lateinit var countriesFeatureApi: CountriesFeatureApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authPhoneEditText.setCountry("RU")
        authPhoneEditText.setCountrySelectButtonListener {
            parentFragmentManager.commit {
                addToBackStack(null)
                setChildEnterAnimations()
                replace(R.id.authFlowContainer, countriesFeatureApi.getStartupFragment())
            }
        }

        authPhoneNextButton.setOnClickListener {
            parentFragmentManager.commit {
                addToBackStack(null)
                setChildEnterAnimations()
                replace(R.id.authFlowContainer, AuthCodeFragment())
            }
        }

        authPhoneScrollView.applyInserts(top = false, bottom = true)
        authPhoneAppBarLayout.setupWithScrollView(authPhoneScrollView)
        authPhoneToolbar.setupWithFragmentManager(requireActivity(), parentFragmentManager)
    }
}