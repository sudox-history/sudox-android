package com.sudox.android.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.viewmodels.ViewModelFactory
import com.sudox.android.common.viewmodels.getViewModel
import dagger.android.support.DaggerFragment

import javax.inject.Inject

class AuthRegisterFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var authRegisterViewModel: AuthRegisterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authRegisterViewModel = getViewModel(viewModelFactory)
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

}