package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.ViewModelFactory
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.database.model.User
import com.sudox.android.data.repositories.main.MAX_DIALOGS_COUNT
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
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

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogsViewModel = getViewModel(viewModelFactory)
        messagesFragment = parentFragment as MessagesFragment

        mainActivity = activity!! as MainActivity

        return inflater.inflate(R.layout.fragment_dialogs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialogsList()
    }

    private fun initDialogsList() {
        val linearLayoutManager = LinearLayoutManager(context)

        // Configure recycler view
        dialogsList.layoutManager = linearLayoutManager
        dialogsList.addItemDecoration(SecondColumnItemDecorator(context!!))
        dialogsList.adapter = dialogsAdapter
        dialogsList.itemAnimator = null

        // Listen for dialog click
        dialogsAdapter.clickCallback = {
            mainActivity.showChatWithUser(User.TRANSFORMATION_TO_USER_CHAT_RECIPIENT(it))
        }

        // Subscribe to initial dialog loading
        dialogsViewModel.initialDialogsLiveData.observe(this, Observer {
            val diffUtil = DialogsDiffUtil(dialogsAdapter.items, it!!)
            val diffResult = DiffUtil.calculateDiff(diffUtil)

            // Update data
            dialogsAdapter.items = ArrayList(it)

            // Notify about updates
            diffResult.dispatchUpdatesTo(dialogsAdapter)
        })

        // Subscribe to partitial load
        dialogsViewModel.partsOfDialogsLiveData.observe(this, Observer {
            if (dialogsAdapter.items.isEmpty()) return@Observer

            val start = dialogsAdapter.items.size - 1
            val end = start + it!!.size

            // Update
            dialogsAdapter.items.addAll(it)
            dialogsAdapter.notifyItemRangeInserted(start, end)
        })

        // Paging & floating action button
        dialogsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, deltaX: Int, deltaY: Int) {
                if (deltaY < 0) {
                    messagesFragment.toggleFloatingActionButton(true)
                } else if (deltaY >= 0) {
                    messagesFragment.toggleFloatingActionButton(false)
                }

                val position = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val updatePosition = linearLayoutManager.itemCount -1

                if (position == updatePosition && linearLayoutManager.itemCount >= MAX_DIALOGS_COUNT) {
                    dialogsViewModel.loadPartOfDialog(linearLayoutManager.itemCount)
                }
            }
        })
    }
}