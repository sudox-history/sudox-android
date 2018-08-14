package com.sudox.android.ui.adapters

import android.app.Activity
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.database.Contact
import kotlinx.android.synthetic.main.card_contact.view.*


class ContactsAdapter(var items: List<Contact>,
                      private val context: Activity) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var contact = items[position]

        if (contact.firstColor != null && contact.secondColor != null) {
            var builder = StringBuilder()
            var names = contact.name.split(" ")

            if (items.size == 1) {
                builder.append(names[0][0])
            }

            if (items.size >= 1) {
                builder.append(names[1][0])
            }

            // Build text
            val text = builder.toString()

            // Get bitmap
            val gradientBitmap = drawGradientBitmap(contact.firstColor!!, contact.secondColor!!, text)

            // Load image
            Glide.with(context).load(gradientBitmap).into(holder.avatar)
        } else {
            TODO("if photo is not gradient")
        }

        holder.name.text = contact.name
        holder.nickname.text = contact.nickname
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

        // Draw text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = 60F
        paint.getTextBounds(text, 0, text.length, textRect)
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        return bitmap;
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!
    }
}
