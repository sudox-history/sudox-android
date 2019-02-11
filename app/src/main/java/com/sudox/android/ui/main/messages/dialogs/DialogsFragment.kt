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
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.ui.main.MainActivity
import com.sudox.design.tablayout.TabLayoutFragment
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.design.recyclerview.decorators.SecondColumnItemDecorator
import kotlinx.android.synthetic.main.fragment_dialogs.*
import javax.inject.Inject

class DialogsFragment @Inject constructor() : TabLayoutFragment() {

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

    override fun onFirstVisible() {
        super.onFirstVisible()

        // Load data ...
        dialogsViewModel.start()
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
                    if (it == null) return@Observer

                    // If Paging disabled
                    if (dialogsAdapter.dialogs.isNotEmpty()) {
                        val initialEndIndex = dialogsAdapter.dialogs.lastIndex
                        val initialSublist = ArrayList(dialogsAdapter.dialogs.subList(0, initialEndIndex))
                        val diffUtil = DialogsDiffUtil(it, initialSublist)
                        val diffResult = DiffUtil.calculateDiff(diffUtil)

                        // Update
                        dialogsAdapter.dialogs = it

                        // Loaded!
                        dialogsListContainer.notifyInitialLoadingDone()
                        diffResult.dispatchUpdatesTo(dialogsAdapter)
                    } else {
                        dialogsAdapter.dialogs = it
                        dialogsAdapter.notifyDataSetChanged()
                        dialogsListContainer.notifyInitialLoadingDone()
                    }
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
                        addDialog(dialog!!)
                    } else {
                        // Try to find dialog with this recipient
                        val indexOf = dialogsAdapter
                                .dialogs
                                .indexOfFirst { it.recipient.uid == dialog!!.recipient.uid }

                        if (indexOf == -1) {
                            addDialog(dialog!!)
                        } else {
                            moveDialogToTop(indexOf, dialog!!)
                        }
                    }
                })

        // Listen new messages
        dialogsViewModel
                .movesToTopMessagesLiveData
                .observe(this, Observer { message ->
                    val indexOf = dialogsAdapter
                            .dialogs
                            .indexOfLast { it.recipient.uid == message!!.getRecipientId() }

                    if (indexOf == -1) {
                        dialogsViewModel.requestNewDialog(message!!)
                    } else {
                        val dialog = dialogsAdapter
                                .dialogs[indexOf]
                                .apply { lastMessage = message!! }

                        // Update
                        moveDialogToTop(indexOf, dialog)
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
                    dialogsViewModel.loadNextDialogs()
                }
            }
        })
    }

    private fun addDialog(dialog: Dialog) {
        val firstPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        val offsetTop = getListTopOffset(firstPos)

        dialogsAdapter.dialogs.add(0, dialog)
        dialogsAdapter.notifyItemInserted(0)

        // Reapply the saved position
        if (firstPos >= 0) {
            linearLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop)
        }
    }

    private fun moveDialogToTop(indexOf: Int, dialog: Dialog) {
        val firstPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        val offsetTop = getListTopOffset(firstPos)

        dialogsAdapter.dialogs.removeAt(indexOf)
        dialogsAdapter.dialogs.add(0, dialog)
        dialogsAdapter.notifyItemMoved(indexOf, 0)
        dialogsAdapter.notifyItemChanged(0)

        // Reapply the saved position
        if (firstPos >= 0) {
            linearLayoutManager.scrollToPositionWithOffset(firstPos, offsetTop)
        }
    }

    private fun getListTopOffset(firstPos: Int): Int {
        var offsetTop = 0

        if (firstPos >= 0) {
            val firstView = linearLayoutManager.findViewByPosition(firstPos)

            // Y scroll
            offsetTop = linearLayoutManager.getDecoratedTop(firstView!!) - linearLayoutManager.getTopDecorationHeight(firstView)
        }

        return offsetTop
    }
}