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

class AvatarView : AppCompatImageView {

    private val oldLetters: String? = null

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

    private fun drawGradientAvatar(data: List<String>, user: User) {
        val firstColor = Color.parseColor("#${data[1]}")
        val secondColor = Color.parseColor("#${data[2]}")
        val text = user.name.getTwoFirstLetters()

        // Preparings ...
        val width = layoutParams.width
        val height = layoutParams.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textRect = Rect()

        // Set shader for background
        paint.shader = LinearGradient(0F, 0F, width.toFloat(), height.toFloat(), firstColor, secondColor, Shader.TileMode.REPEAT)
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)

        // Configure paint for text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = width * 0.3F
        paint.getTextBounds(text, 0, text.length, textRect)

        // Draw text
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        // Set bitmap
        setImageBitmap(bitmap)
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        Glide.with(context)
                .load(bitmap)
                .apply(RequestOptions.circleCropTransform())
                .into(this@AvatarView)
    }
}