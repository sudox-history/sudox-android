package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.ViewModelFactory
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.messages.MessagesFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_dialogs.*
import javax.inject.Inject

class DialogsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var dialogsAdapter: DialogsAdapter
    lateinit var dialogsViewModel: DialogsViewModel
    lateinit var messagesFragment: MessagesFragment

    // Parent activity ...
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogsViewModel = getViewModel(viewModelFactory)
        messagesFragment = parentFragment as MessagesFragment
        mainActivity = activity!! as MainActivity

        return inflater.inflate(R.layout.fragment_dialogs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initDialogsList()

        // Data-logic
        listenData()
    }

    private fun initDialogsList() {
        val linearLayoutManager = LinearLayoutManager(context!!)

        // Configure
        dialogsList.layoutManager = linearLayoutManager
        dialogsList.adapter = dialogsAdapter

        // Configure click listener
        dialogsAdapter.clickedDialogLiveData.observe(this, Observer {
            mainActivity.showChatWithUser(it!!.user)
        })

        // Paging ...
        dialogsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // TODO: Paging
            }
        })
    }

    private fun listenData() {
        // Bind paging dialogs listener
        dialogsViewModel.pagingDialogsLiveData.observe(this, Observer {
            // TODO: Need DiffUtil
        })

        // Bind initial messages listener
        dialogsViewModel.initialDialogsLiveData.observe(this, Observer {
            dialogsAdapter.dialogs = ArrayList(it!!)
            dialogsAdapter.notifyDataSetChanged()
        })

        // Start business logic work
        dialogsViewModel.start()
    }
}