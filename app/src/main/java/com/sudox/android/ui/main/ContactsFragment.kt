package com.sudox.android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.adapters.ContactsAdapter
import com.sudox.android.ui.diffutil.ContactsDiffUtil
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactsViewModel: ContactsViewModel
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initContactsList()
    }

    private fun initContactsList() {
        val adapter = ContactsAdapter(ArrayList(), mainActivity)

        contactsViewModel
                .contactsLoadLiveData()
                .observe(this, Observer {
                    val result = DiffUtil.calculateDiff(ContactsDiffUtil(it, adapter.items))

                    // Update data
                    adapter.items = it
                    result.dispatchUpdatesTo(adapter)
                })
    }
}