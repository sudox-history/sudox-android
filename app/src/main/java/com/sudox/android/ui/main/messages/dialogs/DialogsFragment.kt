package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.ViewModelFactory
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_dialogs.*
import javax.inject.Inject

class DialogsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    // Parent activity ...
    private val mainActivity by lazy { activity!! as MainActivity }
    private val messagesFragment by lazy { parentFragment as MessagesFragment }
    private val dialogsViewModel by lazy { getViewModel<DialogsViewModel>(viewModelFactory) }
    private val dialogsAdapter by lazy { DialogsAdapter(context!!) }
    private val linearLayoutManager by lazy { LinearLayoutManager(context!!) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initDialogsList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialogs, container, false)
    }

    private fun initDialogsList() {
        val recyclerView = dialogsListContainer.recyclerView

        recyclerView.layoutAnimation = null
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = dialogsAdapter
        recyclerView.addItemDecoration(SecondColumnItemDecorator(context!!, false, true))

        // Listen data
        dialogsViewModel.initialDialogsLiveData.observe(this, Observer {
            dialogsAdapter.dialogs = it!!
            dialogsViewModel.isListNotEmpty = it.isNotEmpty()

            // Loaded!
            dialogsAdapter.notifyDataSetChanged()
            dialogsListContainer.notifyInitialLoadingDone()
        })

        // Listen clicks
        dialogsAdapter.clickedDialogLiveData.observe(this, Observer {
            mainActivity.showDialogWithUser(it!!.recipient)
        })

        // Start business logic work
        dialogsViewModel.loadDialogs()
    }
}