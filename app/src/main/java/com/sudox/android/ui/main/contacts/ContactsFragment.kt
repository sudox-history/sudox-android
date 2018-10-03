package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.database.model.Contact
import com.sudox.android.ui.adapters.ContactsAdapter
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.views.decorators.SecondColumnItemDecorator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_contacts.*
import javax.inject.Inject

class ContactsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var contactsAdapter: ContactsAdapter
    lateinit var contactsViewModel: ContactsViewModel
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuring layout components
        initToolbar()
        initContactsList()
    }

    private fun initToolbar() {
        // Ненавижу когда с View'шками происходят неявные для разработчика действия (например: onCreateOptionsMenu)
        contactsToolbar.inflateMenu(R.menu.menu_contacts)
        contactsToolbar.setOnMenuItemClickListener {
            val id = it.itemId

            if (id == R.id.add_contact) {
                // TODO: Add contact
            } else {
                return@setOnMenuItemClickListener false
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun initContactsList() {
        contactsList.layoutManager = LinearLayoutManager(context)
        contactsList.addItemDecoration(SecondColumnItemDecorator(context!!))
        contactsList.adapter = contactsAdapter.apply {
            items = ArrayList()
            items.add(Contact("1", "#233444", "#555555", null, "Антон Янкин", "kerjen#6666"))
            items.add(Contact("2", "#100500", "#666666", null, "Максим Митюшкин", "themax#32053"))
        }
    }
}