package com.sudox.messenger.android.moments.vos

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.withTranslation
import com.sudox.messenger.android.moments.R
import com.sudox.messenger.android.people.common.vos.PeopleVO

class AddMomentVO(
        peopleVO: PeopleVO
) : MomentVO(peopleVO, 0, true) {

    private var addIconClipColor = 0
    private var addIconStrokeColor = 0
    private var addIconStrokeRadius = 0F
    private var addIconClipRadius = 0F
    private var addIconDrawable: Drawable? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.your_story)
    }

    override fun getContentOnCircleAngle(): Double {
        return 7 * Math.PI / 4
    }

    override fun getViewInCenter(context: Context): View {
        addIconClipColor = getColor(context, R.color.addmomentvo_add_icon_clip_color)
        addIconClipRadius = context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_clip_radius).toFloat()
        addIconStrokeRadius = context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_stroke_radius).toFloat()
        addIconStrokeColor = getColor(context, R.color.addmomentvo_add_icon_stroke_color)
        addIconDrawable = getDrawable(context, R.drawable.ic_add)!!.mutate().apply {
            setTint(getColor(context, R.color.addmomentvo_add_icon_tint_color))
            setBounds(0, 0,
                    context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_width),
                    context.resources.getDimensionPixelSize(R.dimen.addmomentvo_add_icon_height)
            )
        }

        return super.getViewInCenter(context)
    }

    override fun drawContentOnCircle(context: Context, canvas: Canvas, centerX: Float, centerY: Float) {
        paint.color = addIconClipColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, addIconClipRadius, paint)

        paint.color = addIconStrokeColor
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(centerX, centerY, addIconStrokeRadius, paint)

        val iconX = centerX - addIconDrawable!!.bounds.exactCenterX()
        val iconY = centerY - addIconDrawable!!.bounds.exactCenterY()

        canvas.withTranslation(x = iconX, y = iconY) {
            addIconDrawable!!.draw(canvas)
        }
    }
}