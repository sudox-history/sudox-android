package com.sudox.android.ui.chats

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.SendMessageState
import com.sudox.android.common.enums.State
import com.sudox.android.common.helpers.showTopSnackbar
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.database.model.Contact
import com.sudox.android.database.model.Message
import com.sudox.android.ui.adapters.MessagesAdapter
import com.sudox.android.ui.splash.SplashActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.include_toolbar_chat.*
import javax.inject.Inject


class ChatActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var adapter: MessagesAdapter

    private lateinit var contact: Contact
    lateinit var items: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatViewModel = getViewModel(viewModelFactory)
        chatViewModel.connectLiveData.observe(this, Observer {
            getConnectState(it)
        })

        initToolbar()
        initListeners()

        chatViewModel
                .getMessagesFromDB(contact.cid)
                .observe(this, Observer(::setMessagesList))
    }

    private fun setMessagesList(messages: List<Message>) {
        items = messages as ArrayList<Message>
        initMessagesList(items)
    }

    private fun getConnectState(connectState: ConnectState) {
        if (connectState == ConnectState.DISCONNECTED) {
            showMessage(getString(R.string.lost_internet_connection))
        } else if (connectState == ConnectState.MISSING_TOKEN || connectState == ConnectState.WRONG_TOKEN) {
            chatViewModel.disconnect()
            showSplashActivity()
        } else if (connectState == ConnectState.CORRECT_TOKEN) {
            showMessage(getString(R.string.connection_restored))
//            chatViewModel.getFirstMessagesFromServer(contact.cid)
//                    .observe(this, Observer {
//                        if (it == State.SUCCESS) {
//                            chatViewModel
//                                    .getMessagesFromDB(contact.cid)
//                                    .observe(this, Observer(::setMessagesList))
//                        }
//                    })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)

        // Super!
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar_chat)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        contact = Contact(intent.getStringExtra("id"), intent.getStringExtra("firstColor"),
                intent.getStringExtra("secondColor"), intent.getStringExtra("avatarUrl"),
                intent.getStringExtra("name"), intent.getStringExtra("nickname"))

        if (contact.firstColor != null && contact.secondColor != null) {
            val builder = StringBuilder()
            val names = contact.name.split(" ")

            if (names.isNotEmpty()) {
                builder.append(names[0][0])
            }

            if (names.size >= 2) {
                builder.append(names[1][0])
            }

            // Build text
            val text = builder.toString()

            // Get bitmap
            val gradientBitmap = drawGradientBitmap(contact.firstColor!!, contact.secondColor!!, text)

            // Load image
            Glide.with(this).load(gradientBitmap).into(chat_avatar)
        } else {
            TODO("if photo is not gradient")
        }

        chat_name.text = contact.name
        chat_status.text = "online"
    }


    private fun initMessagesList(messages: List<Message>) {
        adapter = MessagesAdapter(messages, this)
        messagesList.adapter = adapter
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.stackFromEnd = true
        messagesList.layoutManager = mLayoutManager

        send_message_button.setOnClickListener {
            if (edit_message_field.text.toString() != "")
                chatViewModel.sendSimpleMessage(contact.cid, edit_message_field.text.toString())
                        .observe(this, Observer {
                            if (it.sendMessageState == SendMessageState.SUCCESS) {
                                items.add(it.message!!)
                                edit_message_field.setText("")

                                adapter.items = items
                                adapter.notifyItemChanged(items.size - 1)
                                messagesList.scrollToPosition(items.size - 1)
                            } else {

                            }
                        })
        }
    }

    private fun initListeners() {
        chatViewModel.newMessageLiveData.observe(this, Observer {
            if (it != null && it.userId == contact.cid) {
                items.add(it)

                adapter.items = items
                adapter.notifyItemChanged(items.size - 1)
                messagesList.scrollToPosition(items.size - 1)
            }
        })
    }

    private fun drawGradientBitmap(firstColor: String, secondColor: String, text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Enable antialiasing
        paint.isAntiAlias = true

        // Draw gradient
        paint.shader = LinearGradient(100F, 0F, 100F, 200F,
                Color.parseColor(firstColor), Color.parseColor(secondColor), Shader.TileMode.REPEAT)

        // Draw circle
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), 180F, paint)

        // Text bounds
        val textRect = Rect()

        // Create typeface
        val plain = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)

        // Draw text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = 60F
        paint.typeface = plain
        paint.getTextBounds(text, 0, text.length, textRect)
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        return bitmap
    }

    private fun showSplashActivity() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    fun showMessage(message: String) {
        showTopSnackbar(this, chat_layout, message, TSnackbar.LENGTH_LONG)
    }
}