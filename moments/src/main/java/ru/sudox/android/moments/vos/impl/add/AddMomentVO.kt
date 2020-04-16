package ru.sudox.android.moments.vos.impl.add

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.withTranslation
import ru.sudox.android.moments.R
import ru.sudox.android.moments.vos.impl.MomentVO

class AddMomentVO(
        override var userId: Long,
        override var userName: String,
        override var photoId: Long
) : MomentVO(userId, userName, photoId, 0, true) {

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.your_story)
    }

    override fun getContentOnCircleAngle(): Double {
        return 7 * Math.PI / 4
    }

    override fun createVoStorage(context: Context): Any? {
        return AddMomentVOStorage(
                getColor(context, R.color.addmomentvo_add_icon_clip_color),
                context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_clip_radius).toFloat(),
                context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_stroke_radius).toFloat(),
                getColor(context, R.color.addmomentvo_add_icon_stroke_color),
                getDrawable(context, R.drawable.ic_add)!!.mutate().apply {
                    setTint(getColor(context, R.color.addmomentvo_add_icon_tint_color))
                    setBounds(0, 0,
                            context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_width),
                            context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_height)
                    )
                },
                Paint(Paint.ANTI_ALIAS_FLAG)
        )
    }

    override fun drawContentOnCircle(context: Context, canvas: Canvas, centerX: Float, centerY: Float, storage: Any?) {
        (storage as AddMomentVOStorage).apply {
            paint.color = clipColor
            paint.style = Paint.Style.FILL
            canvas.drawCircle(centerX, centerY, clipRadius, paint)

            paint.color = strokeColor
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(centerX, centerY, strokeRadius, paint)

            val iconX = centerX - drawable.bounds.exactCenterX()
            val iconY = centerY - drawable.bounds.exactCenterY()

            canvas.withTranslation(x = iconX, y = iconY) {
                drawable.draw(canvas)
            }
        }
    }
}