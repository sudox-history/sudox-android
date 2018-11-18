package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.database.model.User
import com.sudox.android.ui.diffutil.ContactsDiffUtil
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import kotlinx.android.synthetic.main.fragment_main_contacts.*
import javax.inject.Inject

class ContactsFragment @Inject constructor() : BaseReconnectFragment() {

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

        // Start listen connection status
        listenForConnection()

        // Start showing ...
        super.onViewCreated(view, savedInstanceState)
    }

    override fun showConnectionStatus(isConnect: Boolean) {
        if (isConnect) {
            contactsToolbar.title = getString(R.string.contacts)
        } else {
            contactsToolbar.title = getString(R.string.wait_for_connect)
        }
    }

    private fun initToolbar() {
        contactsToolbar.inflateMenu(R.menu.menu_contacts)
        contactsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_contact -> showContactAddFragment()
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun initContactsList() {
        contactsAdapter.menuInflater = mainActivity.menuInflater
        contactsAdapter.clickCallback = {
            mainActivity.showChatWithUser(User.TRANSFORMATION_TO_USER_CHAT_RECIPIENT(it))
        }

        contactsList.layoutManager = LinearLayoutManager(context)
        contactsList.addItemDecoration(SecondColumnItemDecorator(context!!, false, false))
        contactsList.adapter = contactsAdapter
        contactsList.itemAnimator = null

        contactsViewModel
                .contactsRepository
                .contactsGetLiveData
                .observe(this, Observer {
                    val diffUtil = ContactsDiffUtil(it!!, contactsAdapter.items)
                    val diffResult = DiffUtil.calculateDiff(diffUtil)

                    // Update data
                    contactsAdapter.items = it

                    // Notify about updates
                    diffResult.dispatchUpdatesTo(contactsAdapter)
                })
    }

    private fun showContactAddFragment() {
        mainActivity.supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .addToBackStack(null)
                .replace(R.id.fragmentMainContainer, ContactAddFragment())
                .commit()
    }
}