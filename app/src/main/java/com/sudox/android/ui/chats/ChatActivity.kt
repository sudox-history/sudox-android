package com.sudox.android.ui.chats

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.showTopSnackbar
import com.sudox.protocol.models.enums.ConnectionState
import com.sudox.android.data.database.model.Contact
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatViewModel = getViewModel(viewModelFactory)
        chatViewModel.messagesRepository.canUpdateLiveData = true
        chatViewModel.connectLiveData.observe(this, Observer {
            getConnectState(it!!)
        })

        initToolbar()
        initMessagesList()
        initSendButton()

        chatViewModel.messagesLiveData.observe(this, Observer {
            adapter.items.addAll(it!!.filter {
                it.userId == contact.cid
            })

            adapter.notifyDataSetChanged()
            messagesList.scrollToPosition(adapter.items.size - 1)
        })

        if (!chatViewModel.loadedContactsIds.contains(contact.cid)) {
            chatViewModel.loadHistoryIntoDatabase(contact.cid, 0, 100)
        } else {
            chatViewModel.loadHistoryFromDatabase(contact.cid)
        }
    }

    private fun initMessagesList() {
        adapter = MessagesAdapter(ArrayList(), this)
        messagesList.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        messagesList.adapter = adapter
    }

    fun initSendButton() {
        send_message_button.setOnClickListener {
            // message_sent_icon
            val text = edit_message_field.text.toString()

            if (text.isNotBlank()) {
                chatViewModel.sendTextMessage(contact.cid, text)
                edit_message_field.setText("")
            }
        }
    }

    private fun getConnectState(connectionState: ConnectionState) {
//        if (connectionState == ConnectionState.DISCONNECTED) {
//            showMessage(getString(R.string.lost_internet_connection))
//        } else if (connectionState == ConnectionState.MISSING_TOKEN || connectionState == ConnectionState.WRONG_TOKEN) {
//            chatViewModel.disconnect()
//            showSplashActivity()
//        } else if (connectionState == ConnectionState.CORRECT_TOKEN) {
//            showMessage(getString(R.string.connection_restored))
//            adapter.items.clear()
//            adapter.notifyDataSetChanged()
//            chatViewModel.loadHistoryIntoDatabase(contact.cid, 0, 100)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)

        // Super!
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
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

    private fun showMessage(message: String) {
        showTopSnackbar(this, chat_layout, message, TSnackbar.LENGTH_LONG)
    }

    override fun onDestroy() {
        chatViewModel.messagesRepository.canUpdateLiveData = false

        // Super!
        super.onDestroy()
    }
}