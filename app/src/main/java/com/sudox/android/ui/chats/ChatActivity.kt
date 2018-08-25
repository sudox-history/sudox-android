package com.sudox.android.ui.chats

import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.database.model.Contact
import com.sudox.android.database.model.Message
import com.sudox.android.ui.adapters.MessagesAdapter
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.include_toolbar_chat.*


class ChatActivity : DaggerAppCompatActivity() {


    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initToolbar()
        initMessagesList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_chat, menu)
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

        val contact = Contact(intent.getStringExtra("id"), intent.getStringExtra("firstColor"),
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
        chat_status.text = contact.name
    }

    private fun initMessagesList() {
        adapter = MessagesAdapter(ArrayList(), this)
        messagesList.adapter = adapter
        val mLayoutManager = LinearLayoutManager(this)

        mLayoutManager.stackFromEnd = true
        messagesList.layoutManager = mLayoutManager

        val items = ArrayList<Message>()

        send_message_button.setOnClickListener{
            if(edit_message_field.text.toString() != "") {
                items.add(Message(1, edit_message_field.text.toString(), "3"))
                edit_message_field.setText("")

                adapter.items = items
                adapter.notifyDataSetChanged()
                messagesList.scrollToPosition(items.size - 1)
            }
        }
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
}