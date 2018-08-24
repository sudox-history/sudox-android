package com.sudox.android.ui.main.chats

import android.graphics.*
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.database.Contact
import com.sudox.android.ui.MainActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.include_toolbar_chat.*
import javax.inject.Inject

class ChatFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_chat, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            android.R.id.home-> mainActivity.goToContactsFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        mainActivity.setSupportActionBar(toolbar_chat)
        mainActivity.supportActionBar!!.setDisplayShowHomeEnabled(true)
        mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle = arguments!!
        val contact = Contact(bundle.getString("id")!!, bundle.getString("firstColor"),
                bundle.getString("secondColor"), bundle.getString("avatarUrl"),
                bundle.getString("name")!!, "")

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