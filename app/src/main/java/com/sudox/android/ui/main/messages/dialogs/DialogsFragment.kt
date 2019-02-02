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
        dialogsViewModel
                .initialDialogsLiveData
                .observe(this, Observer {
                    val initialEndIndex = Math.min(dialogsAdapter.dialogs.size, 20)
                    val initialSublist = ArrayList(dialogsAdapter.dialogs.subList(0, initialEndIndex))
                    val diffUtil = DialogsDiffUtil(it!!, initialSublist)
                    val diffResult = DiffUtil.calculateDiff(diffUtil)

                    // Update
                    dialogsAdapter.dialogs = it

                    // Loaded!
                    dialogsListContainer.notifyInitialLoadingDone()
                    diffResult.dispatchUpdatesTo(dialogsAdapter)
                })

        // Listen paging data
        dialogsViewModel
                .pagingDialogsLiveData
                .observe(this, Observer {
                    val startIndex = dialogsAdapter.dialogs.size - 1

                    dialogsAdapter.dialogs.addAll(it!!)
                    dialogsAdapter.notifyItemRangeInserted(startIndex, it.size)
                })

        // Listen clicks
        dialogsAdapter
                .clickedDialogLiveData
                .observe(this, Observer {
                    mainActivity.showDialogWithUser(it!!.recipient)
                })

        // Listen new dialogs
        dialogsViewModel
                .movesToTopDialogsLiveData
                .observe(this, Observer { dialog ->
                    if (dialogsAdapter.dialogs.isEmpty()) {
                        dialogsAdapter.dialogs.add(dialog!!)
                        dialogsAdapter.notifyItemInserted(0)
                    } else {
                        // Try to find dialog with this recipient
                        val indexOf = dialogsAdapter.dialogs.indexOfFirst { it.recipient.uid == dialog!!.recipient.uid }

                        if (indexOf == -1) {
                            dialogsAdapter.dialogs.add(0, dialog!!)
                            dialogsAdapter.notifyItemInserted(0)
                        } else {
                            dialogsAdapter.dialogs.removeAt(indexOf)
                            dialogsAdapter.dialogs.add(0, dialog!!)
                            dialogsAdapter.notifyItemMoved(indexOf, 0)
                            dialogsAdapter.notifyItemChanged(0)
                        }
                    }
                })

        // Listen new messages
        dialogsViewModel
                .movesToTopMessagesLiveData
                .observe(this, Observer { message ->
                    val indexOf = dialogsAdapter.dialogs.indexOfLast { it.recipient.uid == message!!.getRecipientId() }

                    if (indexOf == -1) {
                        dialogsViewModel.requestDialog(message!!)
                    } else {
                        val dialog = dialogsAdapter.dialogs[indexOf]

                        // Update
                        dialog.lastMessage = message!!
                        dialogsAdapter.dialogs.removeAt(indexOf)
                        dialogsAdapter.dialogs.add(0, dialog)
                        dialogsAdapter.notifyItemMoved(indexOf, 0)
                        dialogsAdapter.notifyItemChanged(0)
                    }
                })

        // Listen recipient updates
        dialogsViewModel
                .recipientsUpdatesLiveData
                .observe(this, Observer { users ->
                    val dialogs = dialogsAdapter.dialogs
                    val size = dialogs.size

                    for (i in 0 until size) {
                        val dialog = dialogs[i]
                        val user = users!!.find { dialog.recipient.uid == it.uid } ?: continue

                        dialogsAdapter.dialogs[i].recipient = user
                        dialogsAdapter.notifyItemChanged(i)
                    }
                })

        // Paging ...
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val updatePosition = dialogsAdapter.dialogs.size - 1

                if (updatePosition - position <= 10 && dialogsAdapter.dialogs.size >= 20) {
                    dialogsViewModel.loadDialogs(updatePosition + 1)
                }
            }
        })

        // Start business logic work
        dialogsViewModel.loadDialogs()
    }
}