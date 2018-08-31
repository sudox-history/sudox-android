package com.sudox.android.ui.main.contacts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.database.model.Contact
import com.sudox.android.ui.adapters.ContactsAdapter
import com.sudox.android.ui.chats.ChatActivity
import com.sudox.android.ui.diffutil.ContactsDiffUtil
import com.sudox.android.ui.main.MainActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.card_add_contact.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.include_search_navbar_addition.*
import javax.inject.Inject



class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: ContactsAdapter
    private lateinit var contactSearch: Contact

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity
        adapter = ContactsAdapter(ArrayList(), mainActivity)

        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(contactsToolbar)

        initSearchAdditionalView()
        initContactsList()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_contacts, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_contact -> {
                searchAdditionalView.toggle()
            }
        }
        return true
    }

    private fun initSearchAdditionalView() {
        searchAdditionalView.startListener = {
            val item = contactsToolbar.menu.findItem(R.id.add_contact)

            if (it) {
                item.setIcon(R.drawable.ic_close)
            } else {
                item.setIcon(R.drawable.ic_add_contact)
            }

            blackOverlayView.toggle(!it)
        }

        blackOverlayView.setOnClickListener {
            searchAdditionalView.toggle(false)
        }
    }

    private fun initContactsList() {
        contactsList.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        contactsList.layoutManager = layoutManager

        contactsViewModel
                .contactsLoadLiveData()
                .observe(this, Observer {
                    searchAdditionalView.toggle(false)
                    if (it.isNotEmpty()) {
                        contactsList.visibility = View.VISIBLE
                        have_not_got_contacts.visibility = View.GONE
                        val result = DiffUtil.calculateDiff(ContactsDiffUtil(it, adapter.items))

                        // Update data
                        adapter.items = it
                        result.dispatchUpdatesTo(adapter)
                    } else {
                        contactsList.visibility = View.GONE
                        have_not_got_contacts.visibility = View.VISIBLE
                    }
                })
    }

    private fun setSearchContact(contact: Contact?) {
        progress_bar.visibility = View.GONE
        if (contact != null) {
            contactSearch = contact
            searchAdditionalView.setSearchContact(contact)
        } else {
            add_contact_hint.visibility = View.VISIBLE
            card_add_contact.visibility = View.GONE
            add_contact_hint.text = getString(R.string.contact_has_not_find)
        }
    }

    private fun initListeners() {
        nicknameEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                progress_bar.visibility = View.VISIBLE
                add_contact_hint.visibility = View.GONE
                contactsViewModel.contactsSearchUserByEmail(nicknameEditText.text.toString())
                        .observe(this, Observer(::setSearchContact))
                return@OnEditorActionListener true
            }
            false
        })

        add_contact_search.setOnClickListener {
            contactsViewModel.contactAdd(contactSearch.cid)
            progress_bar.visibility = View.VISIBLE
            card_add_contact.visibility = View.GONE
        }

        adapter.clickedLongContactLiveData.observe(this, Observer {
            contactsViewModel.removeContact(it)
        })

        adapter.clickedSimpleContactLiveData.observe(this, Observer {
            mainActivity.goToChatActivity( Intent(mainActivity, ChatActivity::class.java).apply {
                putExtra("name", it.name)
                putExtra("firstColor", it.firstColor)
                putExtra("secondColor", it.secondColor)
                putExtra("avatarUrl", it.avatarUrl)
                putExtra("id", it.cid)
                putExtra("nickname", it.nickname)
            })
        })
    }
}