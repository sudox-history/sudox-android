package com.sudox.android.ui.main.messages.dialogs

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.ViewModelFactory
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_dialogs.*
import javax.inject.Inject

class DialogsFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var dialogsAdapter: DialogsAdapter
    lateinit var dialogsViewModel: DialogsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var messagesFragment: MessagesFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogsViewModel = getViewModel(viewModelFactory)
        messagesFragment = parentFragment as MessagesFragment

        return inflater.inflate(R.layout.fragment_dialogs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialogsList()
    }

    private fun initDialogsList() {
        dialogsList.layoutManager = LinearLayoutManager(context)
        dialogsList.addItemDecoration(SecondColumnItemDecorator(context!!))
        dialogsList.adapter = dialogsAdapter

        // Paging & floating action button
        dialogsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, deltaX: Int, deltaY: Int) {
                if (deltaY < 0) {
                    messagesFragment.toggleFloatingActionButton(true)
                } else if (deltaY >= 0) {
                    messagesFragment.toggleFloatingActionButton(false)
                }
            }
        })

        // Load first dialogs ...
        dialogsViewModel.dialogsRepository.loadInitialDialogsFromDb {
            dialogsAdapter.items = it
            dialogsAdapter.notifyItemRangeInserted(0, it.size)

            // Try load dialogs from server
            dialogsViewModel.dialogsRepository.loadInitialDialogsFromServer {
                dialogsAdapter.items = it
                dialogsAdapter.notifyItemRangeInserted(0, it.size)
            }
        }
    }
}