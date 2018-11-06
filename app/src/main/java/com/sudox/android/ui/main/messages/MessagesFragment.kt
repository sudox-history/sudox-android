package com.sudox.android.ui.main.messages

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.contacts.ContactsViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MessagesFragment @Inject constructor(): DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var dialogsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_messages, container, false)
    }
}