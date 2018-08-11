package com.sudox.android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.database.Contact
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.adapters.ContactsAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_contacts.*
import javax.inject.Inject

class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactsViewModel: ContactsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {

        val arrayList = ArrayList<Contact>()
        arrayList.add(Contact(1, null, null, "Антон Янкин", "@kerjen01"))
        arrayList.add(Contact(2, null, null, "Максим Митюшкин", "@the_max7887"))
        arrayList.add(Contact(3, null, null, "Стефания Крымская", "@stef.kryim"))

        val contactsAdapter = ContactsAdapter(arrayList, activity as MainActivity)
        recycler_contacts.layoutManager = LinearLayoutManager(activity)
        recycler_contacts.adapter = contactsAdapter
    }
}