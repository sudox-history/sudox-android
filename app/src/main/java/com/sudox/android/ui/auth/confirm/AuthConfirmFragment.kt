package com.sudox.android.ui.auth.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.sudox.android.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject

// Email bundle key
internal const val EMAIL_BUNDLE_KEY: String = "email"

class AuthConfirmFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_confirm, container, false)
    }

}