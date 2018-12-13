package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ContactAddFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactAddViewModel: ContactAddViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactAddViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }


}