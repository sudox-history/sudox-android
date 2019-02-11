package com.sudox.design.avatar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.AppCompatTextView
import android.util.TypedValue
import com.sudox.android.data.database.model.user.User
import com.sudox.design.helpers.getTwoFirstLetters
import kotlinx.coroutines.*

class TextAvatarView : FrameLayout {

    private var drawingJob: Job? = null
    private val text by lazy {
        AppCompatTextView(context).apply {
            layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER)
        }
    }

    private val background by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        addView(background)
        addView(text.apply {
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (!changed) return

        // Shape ...
        setBackground(ShapeDrawable(OvalShape()).apply {
            intrinsicHeight = measuredHeight
            intrinsicWidth = measuredWidth
            bounds = Rect(measuredHeight / 2, measuredWidth / 2, measuredHeight / 2, measuredWidth / 2)
            paint.color = Color.WHITE
        })

        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.min(measuredHeight, measuredWidth) * 0.3F)
    }

    fun bindLetters(textForShowing: String) {
        var letters = textForShowing.getTwoFirstLetters()

        // Update only if needed
        if (text.text.toString() != letters) {
            text.text = letters
        }
    }

    fun bindUser(user: User) {
        val data = user.photo.split(".")
        val type = data[0]

        // Render ...
        if (type == "col") {
            drawingJob?.cancel()
            drawingJob = drawGradientAvatar(data)

            // Bind letters
            text.setTextColor(Color.WHITE)
            bindLetters(user.name)
        }
    }

    private fun drawGradientAvatar(data: List<String>) = GlobalScope.launch(Dispatchers.IO) {
        val firstColor = Color.parseColor("#${data[1]}")
        val secondColor = Color.parseColor("#${data[2]}")

        // Preparings ...
        val width = layoutParams.width
        val height = layoutParams.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        val paintWidth = width.toFloat()
        val paintHeight = height.toFloat()
        val xRadius = paintWidth / 2
        val yRadius = paintHeight / 2

        // Set shader for background
        paint.shader = LinearGradient(0F, 0F, paintWidth, paintHeight, firstColor, secondColor, Shader.TileMode.REPEAT)
        canvas.drawRoundRect(0F, 0F, paintWidth, paintHeight, xRadius, yRadius, paint)

        // Set bitmap
        GlobalScope.launch(Dispatchers.Main) { background.setImageBitmap(bitmap) }
    }

    override fun onDetachedFromWindow() {
        drawingJob?.cancel()

        // Super!
        super.onDetachedFromWindow()
    }
}