package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main_contacts.*
import javax.inject.Inject

class ContactsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_main_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()

        // Start showing ...
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initToolbar() {
        contactsToolbar.inflateMenu(R.menu.menu_contacts)
        contactsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_contact_menu_item -> showContactAddFragment()
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun showContactAddFragment() {
        mainActivity
                .supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentMainContainer, ContactAddFragment())
                .commit()
    }
}