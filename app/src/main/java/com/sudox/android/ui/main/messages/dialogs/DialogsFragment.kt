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
import java.util.*
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
        dialogsAdapter.items = getFakeDialogs(15)
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

    private fun getFakeDialogs(count: Int): List<User> {
        val items = ArrayList<User>()
        val random = Random()
        val colors = arrayListOf("col.f093fb.f5576c", "col.00f2fe.4facfe", "col.fee140.fa709a", "col.38f9d7.43e97b", "col.ffb199.ff0844")

        for (i in 0..count) {
            items.add(User().apply {
                avatar = colors[random.nextInt(5)]
                name = "${generateRandomWord(7)} ${generateRandomWord(7)}"
                nickname = generateRandomWord(20)
            })
        }
        return items
    }


    private fun generateRandomWord(lenght: Int): String {
        val random = Random()
        val alphabet = "abcdefghijklmnopqrstuvwxyz"
        val word = StringBuilder()

        word.append(alphabet[random.nextInt(26)].toUpperCase())

        for (i in 1..lenght) {
            word.append(alphabet[random.nextInt(26)])
        }

        return word.toString()
    }
}