package com.sudox.android.ui.main.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.adapters.ContactsAdapter
import com.sudox.android.ui.diffutil.ContactsDiffUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_contacts.*
import javax.inject.Inject

class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactsViewModel: ContactsViewModel
    lateinit var mainActivity: MainActivity

    private lateinit var adapter: ContactsAdapter

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



        black_bg.setOnClickListener {
            contactsToolbar.menu.findItem(R.id.add_contact).setIcon(R.drawable.ic_add_contact)
            black_bg.animate().setDuration(300).alpha(0f).withEndAction {
                black_bg.visibility = View.GONE
                contactsList.isNestedScrollingEnabled = true
            }.withStartAction {
                black_bg.isClickable = false
            }
            searchAdditionalView.toggle()
            state = false
        }
        black_bg.isClickable = false

        initContactsList()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_contacts, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private var state: Boolean = false

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_contact -> {
                state = if (!state) {
                    contactsToolbar.menu.findItem(R.id.add_contact).setIcon(R.drawable.ic_close)
                    black_bg.animate().setDuration(300).alpha(0.3f).withStartAction {
                        black_bg.visibility = View.VISIBLE
                        contactsList.isNestedScrollingEnabled = false
                    }.withEndAction {
                        black_bg.isClickable = true
                    }
                    true
                } else {
                    contactsToolbar.menu.findItem(R.id.add_contact).setIcon(R.drawable.ic_add_contact)
                    black_bg.animate().setDuration(300).alpha(0f).withEndAction {
                        black_bg.visibility = View.GONE
                        contactsList.isNestedScrollingEnabled = true
                    }.withStartAction {
                        black_bg.isClickable = false
                    }
                    false
                }
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
                    val result = DiffUtil.calculateDiff(ContactsDiffUtil(it, adapter.items))

                    // Update data
                    adapter.items = it
                    result.dispatchUpdatesTo(adapter)
                })
    }
}