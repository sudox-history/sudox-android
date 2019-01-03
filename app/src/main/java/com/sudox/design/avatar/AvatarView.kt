package com.sudox.design.avatar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sudox.android.data.database.model.user.User
import com.sudox.design.helpers.getTwoFirstLetters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch

class AvatarView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun bindUser(user: User) {
        val data = user.photo.split(".")
        val type = data[0]

        // Render ...
        if (type == "col") {
            drawGradientAvatar(data, user)
        } else {
//            TODO("Unsupported avatar type ...")
        }
    }

    private fun drawGradientAvatar(data: List<String>, user: User) = GlobalScope.launch(Dispatchers.IO) {
        val firstColor = Color.parseColor("#${data[1]}")
        val secondColor = Color.parseColor("#${data[2]}")
        val text = user.name.getTwoFirstLetters()

        // Preparings ...
        val width = layoutParams.width
        val height = layoutParams.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        val textRect = Rect()
        val paintWidth = width.toFloat()
        val paintHeight = height.toFloat()
        val xRadius = paintWidth / 2
        val yRadius = paintHeight / 2

        // Set shader for background
        paint.shader = LinearGradient(0F, 0F, paintWidth, paintHeight, firstColor, secondColor, Shader.TileMode.REPEAT)
        canvas.drawRoundRect(0F, 0F, paintWidth, paintHeight, xRadius, yRadius, paint)

        // Configure paint for text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = paintWidth * 0.3F
        paint.getTextBounds(text, 0, text.length, textRect)

        // Draw text
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        // Set bitmap
        GlobalScope.launch(Dispatchers.Main) { setImageBitmap(bitmap) }
    }
}