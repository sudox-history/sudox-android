package com.sudox.messenger.auth.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sudox.design.widgets.navbar.button.NavigationBarButtonIconDirection
import com.sudox.messenger.auth.R
import com.sudox.messenger.core.AppActivity

class AuthPhoneFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        configureNavbar()

        return inflater.inflate(R.layout.fragment_auth_phone, container, false)
    }

    private fun configureNavbar() {
        val activity = activity as AppActivity
        val navigationBar = activity.getNavigationBar()
        val buttonEnd = navigationBar.buttonsEnd[0]!!

        navigationBar.resetView()
        buttonEnd.setText("Далее")
        buttonEnd.setIconDirection(NavigationBarButtonIconDirection.RIGHT)
        buttonEnd.setIconDrawableRes(R.drawable.ic_arrow_nav_end)
        buttonEnd.isClickable = true
        buttonEnd.visibility = View.VISIBLE
    }
}