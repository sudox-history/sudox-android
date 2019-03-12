package com.sudox.android.ui.messages.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.formatMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.ui.messages.MessagesInnerActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_messages_dialog.*
import javax.inject.Inject

class DialogFragment @Inject constructor() : DaggerFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Список сообщений
    private val linearLayoutManager by lazy { LinearLayoutManager(context!!).apply { stackFromEnd = true } }
    private val dialogAdapter by lazy { DialogAdapter(context!!) }
    private var isInitialized: Boolean = false

    private lateinit var messagesInnerActivity: MessagesInnerActivity
    private lateinit var recipientUser: User
    private lateinit var dialogViewModel: DialogViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        messagesInnerActivity = activity as MessagesInnerActivity
        recipientUser = (arguments!!.getSerializable(MessagesInnerActivity.RECIPIENT_USER_EXTRA) as User?)!!
        dialogViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_messages_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        initMessagesList()

        // Start business logic work
        dialogViewModel.start(recipientUser.uid)
    }

    private fun initToolbar() {
        chatToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
        chatToolbar.inflateMenu(R.menu.menu_messages_chat_user)
        chatToolbar.setOnMenuItemClickListener(this)

        // Bind data
        bindRecipient()

        // Bind recipient updates
        dialogViewModel.recipientUpdatesLiveData.observe(this, Observer {
            recipientUser = it!!

            // Update
            bindRecipient()
        })
    }

    private fun initMessagesList() {
        val chatMessagesList = dialogMessagesContainer.recyclerView
        val chatMessagesPadding = (10 * resources.displayMetrics.density).toInt()

        chatMessagesList.setPadding(chatMessagesPadding, chatMessagesPadding, chatMessagesPadding, chatMessagesPadding)
        chatMessagesList.layoutManager = linearLayoutManager
        chatMessagesList.adapter = dialogAdapter
        chatMessagesList.itemAnimator = null
        chatMessagesList.layoutAnimation = null
        chatMessagesList.clipToPadding = false

        // Bind initial messages listener
        dialogViewModel.initialDialogHistoryLiveData.observe(this, Observer {
            val initialEndIndex = Math.min(
                    Math.max(Math.max(dialogAdapter.messages.size, 20) - 1, 0),
                    dialogAdapter.messages.size)

            val initialStartIndex = Math.max(0, initialEndIndex - 20)
            val initialSublist = ArrayList(dialogAdapter.messages.subList(initialStartIndex, initialEndIndex))
            val diffUtil = DialogDiffUtil(it!!, initialSublist)
            val diffResult = DiffUtil.calculateDiff(diffUtil)

            // Update
            dialogAdapter.messages = it

            // Loaded!
            dialogMessagesContainer.notifyInitialLoadingDone()
            diffResult.dispatchUpdatesTo(dialogAdapter)

            // Listen messages sending requests
            if (!isInitialized) {
                dialogViewModel.newDialogMessagesLiveData.observe(this, Observer {
                    val messages = it!!.first
                    val scrollToDown = it.second

                    dialogAdapter.messages.addAll(messages)
                    dialogAdapter.notifyItemRangeInserted(dialogAdapter.messages.size - messages.size, messages.size)
                    dialogAdapter.notifyItemChanged(dialogAdapter.messages.size - messages.size - 1)

                    if (scrollToDown) {
                        chatMessagesList.scrollToPosition(dialogAdapter.messages.size - messages.size)
                    }
                })

                // Start listen to new messages
                listenMessagesSendingRequests()
                isInitialized = true
            }
        })

        // Bind paging messages listener
        dialogViewModel.pagingDialogHistoryLiveData.observe(this, Observer {
            dialogAdapter.messages.addAll(0, it!!)
            dialogAdapter.notifyItemRangeInserted(0, it.size)
        })

        // Bind messages sending status listener
        dialogViewModel.sentMessageLiveData.observe(this, Observer { message ->
            val position = dialogAdapter.messages.indexOfFirst { it.lid == message!!.lid }

            // Message already saved :)
            if (position >= 0) {
                dialogAdapter.messages[position] = message!!
                dialogAdapter.notifyItemChanged(position)
            } else {
                dialogAdapter.messages.add(message!!)
                dialogAdapter.notifyItemInserted(dialogAdapter.messages.size - 1)
                dialogAdapter.notifyItemChanged(dialogAdapter.messages.size - 2)
                chatMessagesList.scrollToPosition(dialogAdapter.messages.size - 1)
            }
        })

        // Paging ...
        chatMessagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                val updatePosition = dialogAdapter.messages.size - 1

                if (visibleItemCount + (totalItemCount - firstVisibleItemPosition) >= totalItemCount && dialogAdapter.messages.size >= 20) {
                    dialogViewModel.loadMessages(updatePosition + 1)
                }
            }
        })
    }

    private fun listenMessagesSendingRequests() {
        chatSendMessageButton.setOnClickListener {
            val text = formatMessage(chatMessageTextField.text.toString())

            // Filter empty text
            if (text.isNotEmpty()) {
                dialogViewModel.sendTextMessage(recipientUser.uid, text)

                // Clear sent text
                chatMessageTextField.text = null
            }
        }

        attachFileButton.setOnClickListener {
            Toast.makeText(messagesInnerActivity, R.string.function_in_development, Toast.LENGTH_LONG).show()
        }

    }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ring_user -> Toast.makeText(messagesInnerActivity, R.string.function_in_development, Toast.LENGTH_LONG).show()
            else -> return false
        }
        return true
    }

    private fun bindRecipient() {
        chatPeerAvatar.bindUser(recipientUser)
        chatPeerName.installText(recipientUser.name)
        chatPeerStatus.installText(recipientUser.nickname)
    }
}