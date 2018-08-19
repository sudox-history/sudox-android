package com.sudox.android.ui.main.contacts

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.database.Contact
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.adapters.ContactsAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.include_search_navbar_addition.*
import javax.inject.Inject

class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity
        adapter = ContactsAdapter(ArrayList(), mainActivity)

        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_contact -> {
                searchAdditionalView.toggle()
            }
        }
        return true
    }


    private fun initContactsList() {
        contactsList.adapter = adapter
        contactsList.layoutManager = LinearLayoutManager(activity)

        contactsViewModel
                .contactsLoadLiveData()
                .observe(this, Observer {

                })
    }

    private fun setSearchContact(contact: Contact?) = searchAdditionalView.setSearchContact(contact)

    private fun initListeners() {
        val nicknameRegex = ".+#.*".toRegex()

        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.matches(nicknameRegex)) {
                    nicknameEditText.inputType = InputType.TYPE_CLASS_NUMBER
                } else {
                    nicknameEditText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        })

        nicknameEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                contactsViewModel.contactsSearchUserByNickname(nicknameEditText.text.toString())
                        .observe(this, Observer(::setSearchContact))
                return@OnEditorActionListener true
            }
            false
        })
    }


}