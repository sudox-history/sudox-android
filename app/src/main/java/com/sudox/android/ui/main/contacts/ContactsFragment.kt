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
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import com.sudox.protocol.models.enums.ConnectionState
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main_contacts.*
import kotlinx.android.synthetic.main.fragment_main_contacts.view.*
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
        super.onViewCreated(view, savedInstanceState)

        // Configuring layout components
        initToolbar()
        initContactsList()
        initContactsExpandedView()
    }

    override fun onResume() {
        super.onResume()

        // Проверка, оффлайн ли сейчас режим
        if (!contactsViewModel.contactsRepository.protocolClient.isValid()) {
            contactsToolbar.title = getString(R.string.wait_for_connect)
        }
    }

    private fun initToolbar() {
        // Ненавижу когда с View'шками происходят неявные для разработчика действия (например: onCreateOptionsMenu)
        // Антон от 15-го октября: Справедливые слова, Макс, справедливые!

        // Настройка меню
        contactsToolbar.contactsToolbar.inflateMenu(R.menu.menu_contacts)
        contactsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_contact -> contactAddExpandedView.toggle()
            }

            // Все прошло успешно
            return@setOnMenuItemClickListener true
        }
    }

    private fun initContactsList() {
        contactsAdapter.menuInflater = mainActivity.menuInflater
        contactsAdapter.clickCallback = {
            mainActivity.showChatWithUser(User.TRANSFORMATION_TO_USER_CHAT_RECIPIENT(it))
        }

        // Init recycler view
        contactsList.layoutManager = LinearLayoutManager(context)
        contactsList.addItemDecoration(SecondColumnItemDecorator(context!!))
        contactsList.adapter = contactsAdapter

        // Подписываемся на обновление данных
        contactsViewModel
                .contactsRepository
                .contactsGetLiveData
                .observe(this, Observer {
                    val diffUtil = ContactsDiffUtil(it!!, contactsAdapter.items)
                    val diffResult = DiffUtil.calculateDiff(diffUtil)

                    // Update data ...
                    contactsAdapter.items = it


                    // Узнаем количество контактов
                    val amount = it.size

                    // Применяем изменения, учитывая падежи
                    if (amount == 0){
                        contactsAmount.visibility = View.GONE
                    } else {
                        contactsAmount.visibility = View.VISIBLE
                        contactsAmount.text = resources.getQuantityString(R.plurals.contacts_amount, it.size, it.size)
                    }

                    // Notify chatAdapter about update
                    diffResult.dispatchUpdatesTo(contactsAdapter)
                })

        contactsViewModel
                .contactsRepository
                .protocolClient
                .connectionStateLiveData
                .observe(this, Observer {
                    if (it == ConnectionState.CONNECTION_CLOSED) {
                        contactsToolbar.title = getString(R.string.wait_for_connect)
                    } else if (it == ConnectionState.HANDSHAKE_SUCCEED) {
                        contactsToolbar.title = getString(R.string.contacts)
                    }
                })
    }

    private fun initContactsExpandedView() {
        val addContactMenuItem = contactsToolbar.menu.findItem(R.id.add_contact)

        // Слушатель открытия/закрытия меню добавления контакта.
        contactAddExpandedView.expandingCallback = {
            addContactMenuItem.setIcon(if (it) R.drawable.ic_close else R.drawable.ic_add_contact)
        }
    }
}