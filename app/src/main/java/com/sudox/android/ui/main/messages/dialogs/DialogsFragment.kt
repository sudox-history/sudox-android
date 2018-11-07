package com.sudox.android.ui.main.messages.dialogs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.User
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_dialogs.*
import javax.inject.Inject

class DialogsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var dialogsAdapter: DialogsAdapter

    private lateinit var messagesFragment: MessagesFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        messagesFragment = parentFragment as MessagesFragment

        return inflater.inflate(R.layout.fragment_dialogs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTestDialogs()
    }

    private fun initTestDialogs() {
        dialogsList.layoutManager = LinearLayoutManager(context)
        dialogsList.addItemDecoration(SecondColumnItemDecorator(context!!))
        dialogsAdapter.items = getFakeDialogs()
        dialogsList.adapter = dialogsAdapter

        dialogsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    messagesFragment.hideOrShowFAB(false)
                } else {
                    messagesFragment.hideOrShowFAB(true)
                }
            }
        })
    }

    private fun getFakeDialogs(): List<User> {
        val items = ArrayList<User>()
        for (i in 0..15) {
            items.add(User().apply {
                name = "Тестируемый Объект"
                nickname = "Привет, что делаешь?"
            })
        }
        return items
    }
}