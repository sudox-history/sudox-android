package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.android.synthetic.main.view_contact_add_expanded.view.*
import javax.inject.Inject

class ContactAddExpandedView : ExpandedView {

    @Inject
    lateinit var contactsRepository: ContactsRepository

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        turnBlackOverlay = true

        // Inflate view
        inflate(context, R.layout.view_contact_add_expanded, this)

        // Заинжектим здесь все к хуям
        ApplicationLoader.component.inject(this)

        // Настроим View'шки
        initEmailEditText()
    }

    private fun initEmailEditText() {
        emailEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_SEARCH) searchContact(emailEditText.text.toString())

            // Клавиатуру скрываем
            return@setOnEditorActionListener false
        }
    }

    private fun searchContact(email: String) {
        contactsRepository.searchContactByEmail(email, {
            foundedContactAddExpandedView.bindData(it)
            foundedContactAddExpandedView.show()
        }, {
            if (it == Errors.INVALID_USER) {
                contactAddStatusExpandedView.showMessage(context.getString(R.string.contact_has_not_find))
            } else {
                contactAddStatusExpandedView.showMessage(context.getString(R.string.unknown_error))
            }

            foundedContactAddExpandedView.hide()
        })
    }

    override fun clear() {
        foundedContactAddExpandedView.hide()

        // Clear all
        emailEditText.setText("")
    }
}