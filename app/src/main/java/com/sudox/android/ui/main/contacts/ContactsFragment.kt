package com.sudox.android.ui.main.contacts

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import com.sudox.design.navigation.NavigationRootFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_contacts.*
import javax.inject.Inject

class ContactsFragment @Inject constructor() : NavigationRootFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var contactsAdapter: ContactsAdapter

    private val contactsViewModel by lazy { getViewModel<ContactsViewModel>(viewModelFactory) }
    private val mainActivity by lazy { activity as MainActivity }
    private var syncConfirmationDialog: AlertDialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initContactsList()

        // Listen data updates
        contactsViewModel.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_contacts, container, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == ContactsViewModel.CONTACT_SYNC_PERMISSION_REQUEST
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Есть права - есть синхронизация
            contactsViewModel.syncContacts(activity!!, this,true)
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

                    // Update data
                    diffResult.dispatchUpdatesTo(contactsAdapter)

                    // Update count ...
                    contactsAdapter.notifyItemChanged(it.lastIndex + 1)
                })

        contactsViewModel
                .contactsActionLiveData
                .observe(this, Observer {
                    if (it == ContactsAction.SHOW_NO_CONTACTS_IN_BOOK_DIALOG) {
                        AlertDialog.Builder(context!!)
                                .setTitle(R.string.title_oops)
                                .setMessage(R.string.empty_contacts_book)
                                .setPositiveButton(R.string.ok, null)
                                .show()
                    } else if(it == ContactsAction.INIT_SYNC){
                        contactsRecyclerViewContainer.notifyInitialLoadingStart()
                    } else if(it == ContactsAction.STOP_SYNC){
                        contactsRecyclerViewContainer.notifyInitialLoadingDone()
                    }
                })

        contactsAdapter
                .editableContactsLiveData
                .observe(this, Observer {
                    mainActivity
                            .fragmentNavigator
                            .showChildFragment(ContactAddFragment()
                                    .apply {
                                        inEditMode = true
                                        editableUser = it!!
                                    })
                })

        contactsAdapter
                .clickedContactLiveData
                .observe(this, Observer {
                    mainActivity.showDialogWithUser(it!!)
                })


    }

    private fun initToolbar() {
        mainActivity.mainToolbar.reset()
        mainActivity.mainToolbar.setTitle(R.string.contacts)
        mainActivity.mainToolbar.inflateMenu(R.menu.menu_contacts)
        mainActivity.mainToolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_contact_menu_item -> mainActivity
                    .fragmentNavigator
                    .showChildFragment(ContactAddFragment())

            // Синхронизация (на Android M метод может вызвать ещё из onRequestPermissionsResult)
            R.id.contacts_sync_item -> {
                syncConfirmationDialog = AlertDialog.Builder(context!!)
                        .setTitle(R.string.sync_contacts)
                        .setMessage(R.string.contacts_sync_confirmation_text)
                        .setPositiveButton(R.string.yes_confirmation) { _, _ -> contactsViewModel.syncContacts(activity!!, this) }
                        .setNegativeButton(R.string.no_confirmation) { _, _ -> }
                        .create()

                syncConfirmationDialog!!.show()
            }

            R.id.contacts_search_item -> Toast.makeText(mainActivity, R.string.function_in_development, Toast.LENGTH_LONG).show()
        }

        return true
    }

    override fun onFragmentOpened() {
        initToolbar()
    }

    override fun onFragmentClosed() {
        if (syncConfirmationDialog != null && syncConfirmationDialog!!.isShowing) {
            syncConfirmationDialog!!.dismiss()
        }
    }
}