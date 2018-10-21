package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.android.synthetic.main.expanded_contact_add_view.view.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class ContactAddExpandedView : ExpandedView {

    @Inject
    lateinit var contactsRepository: ContactsRepository
    lateinit var foundedContact: Contact

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        turnBlackOverlay = true

        // Inflate view
        inflate(context, R.layout.expanded_contact_add_view, this)

        // Заинжектим здесь все к хуям
        ApplicationLoader.component.inject(this)

        // Настроим View'шки
        initEmailEditText()
        initAddButton()
    }

    private fun initEmailEditText() {
        emailEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_SEARCH) searchContact(emailEditText.text.toString())

            // Клавиатуру не скрываем
            return@setOnEditorActionListener false
        }
    }

    private fun initAddButton() {
        foundedContactAddExpandedView.contactAddButtonClickCallback = {
            contactsRepository.addContact(foundedContact.uid, {
                GlobalScope.launch(Dispatchers.Main) { clear() }
            }) {
                // TODO: Вывести ошибку
            }
        }
    }

    private fun searchContact(email: String) {
        contactsRepository.searchContactByEmail(email, {
            foundedContact = it

            // Show contact
            showFoundedContact(it)
        }, {
            // TODO: Вывести ошибку
        })
    }

    private fun showFoundedContact(contact: Contact) = GlobalScope.launch(Dispatchers.Main) {
        foundedContactAddExpandedView.bindData(contact)
        foundedContactAddExpandedView.show()
    }

    override fun clear() {
        foundedContactAddExpandedView.hide()

        // Clear all
        emailEditText.setText("")
    }
}