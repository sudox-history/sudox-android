package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main_contacts.*
import javax.inject.Inject

class ContactsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var contactsAdapter: ContactsAdapter

    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_main_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        initContactsList()

        // Listen data updates
        contactsViewModel.start()

        // Start showing ...
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == ContactsViewModel.CONTACT_SYNC_PERMISSION_REQUEST
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Есть права - есть синхронизация
            contactsViewModel.syncContacts(activity!!, true)
        }
    }

    private fun initContactsList() {
        contactsAdapter.menuInflater = activity!!.menuInflater
        contactsRecyclerViewContainer
                .recyclerView
                .apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = contactsAdapter
                    itemAnimator = null
                    addItemDecoration(SecondColumnItemDecorator(context, false, true))
                }

        contactsViewModel
                .contactsLiveData
                .observe(this, Observer {
                    val diffUtil = ContactsDiffUtil(it!!, contactsAdapter.contacts)
                    val diffResult = DiffUtil.calculateDiff(diffUtil)

                    // Update
                    contactsAdapter.contacts = it

                    // Loaded!
                    contactsRecyclerViewContainer.notifyInitialLoadingDone()

                    // Update
                    diffResult.dispatchUpdatesTo(contactsAdapter)
                })
    }

    private fun initToolbar() {
        contactsToolbar.inflateMenu(R.menu.menu_contacts)
        contactsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_contact_menu_item -> mainActivity
                        .fragmentNavigator
                        .showChildFragment(ContactAddFragment())

                // Синхронизация (на Android M метод может вызвать ещё из onRequestPermissionsResult)
                R.id.contacts_sync_item -> contactsViewModel.syncContacts(activity!!)
            }

            return@setOnMenuItemClickListener true
        }
    }
}