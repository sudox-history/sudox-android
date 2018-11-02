package com.sudox.android.ui.main.contacts.view

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.design.navigation.toolbar.expanded.ExpandedView
import kotlinx.android.synthetic.main.view_contact_add_expanded.view.*
import javax.inject.Inject

class ContactAddExpandedView : ExpandedView {

    @Inject
    lateinit var usersRepository: UsersRepository

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
        initQueryEditText()
    }

    private fun initQueryEditText() {
        queryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_SEARCH) searchContact(queryEditText.text.toString())

            // Клавиатуру скрываем
            return@setOnEditorActionListener false
        }
    }

    private fun searchContact(query: String) {
        usersRepository.searchUser(query) {
            if (it.isSuccess()) {
                foundedContactAddExpandedView.bindData(it)
                foundedContactAddExpandedView.show()
            } else {
                contactAddStatusExpandedView.showMessage(context.getString(when {
                    it.error == Errors.INVALID_USER -> R.string.contact_has_not_find
                    it.error == Errors.INVALID_PARAMETERS -> R.string.wrong_email_format
                    else -> R.string.unknown_error
                }))

                foundedContactAddExpandedView.hide()
            }
        }
    }

    override fun clear() {
        foundedContactAddExpandedView.hide()

        // Clear all
        queryEditText.setText("")
    }
}