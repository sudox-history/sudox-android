package com.sudox.design.avatar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import com.sudox.android.data.database.model.user.User
import com.sudox.design.helpers.getTwoFirstLetters
import android.graphics.RectF

class UserAvatarView : ImageView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun bindUser(user: User) {
        val data = user.photo.split(".")
        val type = data[0]

        // Render ...
        val bitmap = if (type == "col") {
            drawGradientAvatar(data, user)
        } else {
            TODO("Unsupported avatar type ...")
        }

        // Show ...
        setImageBitmap(bitmap)
    }

    private fun drawGradientAvatar(data: List<String>, user: User): Bitmap {
        val firstColor = Color.parseColor(data[1])
        val secondColor = Color.parseColor(data[2])
        val text = user.name
                .getTwoFirstLetters()
                .toUpperCase()

        // Preparings ...
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textRect = Rect()

        // Set shader for background
        paint.shader = LinearGradient(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), firstColor, secondColor, Shader.TileMode.REPEAT)

        // Draw background
        canvas.drawRect(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)

        // Configure paint for text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = measuredWidth * 0.3F
        paint.getTextBounds(text, 0, text.length, textRect)

        // Draw text
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        // Return ...
        return bitmap
    }

    override fun dispatchDraw(canvas: Canvas) {
        val path = Path()
        val rect = RectF(0F, 0F, width.toFloat(), height.toFloat())

        // Clip to circle
        path.addRoundRect(rect, width.toFloat() * 0.5F, height.toFloat() * 0.5F, Path.Direction.CW)
        canvas.clipPath(path)
    }
}