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
import org.json.JSONException


class ContactsAdapter(var items: List<Contact>,
                      private val context: Activity) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (items[position].firstColor != null) {

            val text = "${items[position].name.split(" ")[0][0]}${items[position].name.split(" ")[1][0]}"

            Glide.with(context)
                    .load(setGradientColor(items[position].firstColor!!,
                            items[position].secondColor!!, text)).into(holder.avatar)

            //TODO:refactor this code
        } else {

        }

        holder.name.text = items[position].name
        holder.nickname.text = items[position].nickname
    }

    private fun setGradientColor(firstColor: String, secondColor: String, text: String): Bitmap {
        val imageBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val imageCanvas = Canvas(imageBitmap)
        val paint = getGradientPaint(firstColor, secondColor)
        imageCanvas.drawCircle((imageBitmap.width / 2).toFloat(), (imageBitmap.height / 2).toFloat(), 180f, paint)
        paint.shader = null
        paint.color = Color.parseColor("#ffffff")
        drawTextCentred(imageCanvas, paint, text, 60, (imageBitmap.width / 2).toFloat(), (imageBitmap.height / 2).toFloat())

        return imageBitmap
    }

    private fun getGradientPaint(firstColor: String, secondColor: String): Paint {
        val paint = Paint()
        try {
            paint.shader = LinearGradient(0f, 0f, 300f, 300f, Color.parseColor(firstColor),
                    Color.parseColor(secondColor), Shader.TileMode.REPEAT)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return paint
    }

    private val textBounds: Rect = Rect()

    private fun drawTextCentred(canvas: Canvas, paint: Paint, text: String, textSize: Int, cx: Float, cy: Float) {
        paint.textSize = textSize.toFloat()
        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.nickname!!
    }
}
