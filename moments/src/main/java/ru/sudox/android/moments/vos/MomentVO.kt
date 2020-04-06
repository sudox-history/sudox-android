package ru.sudox.android.moments.vos

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import ru.sudox.design.circularupdatableview.vos.CircularUpdatableViewVO
import ru.sudox.design.circularupdatableview.vos.NOT_SHOW_CONTENT_ON_STROKE_ANGLE
import ru.sudox.android.moments.R
import ru.sudox.android.people.common.views.AvatarImageView
import ru.sudox.android.people.common.vos.PeopleVO

open class MomentVO(
        val peopleVO: PeopleVO,
        val publishTime: Long,
        val isViewed: Boolean
) : CircularUpdatableViewVO {

    override fun getTitle(context: Context): String? {
        return peopleVO.userName
    }

    override fun isViewInCenterTypeSame(view: View): Boolean {
        return view is AvatarImageView
    }

    override fun getContentOnCircleAngle(): Double {
        return NOT_SHOW_CONTENT_ON_STROKE_ANGLE
    }

    override fun drawContentOnCircle(context: Context, canvas: Canvas, centerX: Float, centerY: Float) {
    }

    override fun unbindViewInCenter(view: View) {
        (view as AvatarImageView).vo = null
    }

    override fun bindViewInCenter(view: View) {
        (view as AvatarImageView).let {
            it.layoutParams = ViewGroup.LayoutParams(
                    view.context.resources.getDimensionPixelSize(R.dimen.momentvo_photo_width),
                    view.context.resources.getDimensionPixelSize(R.dimen.momentvo_photo_height)
            )

            it.vo = peopleVO
        }
    }

    override fun getViewInCenter(context: Context): View {
        return AvatarImageView(context)
    }

    override fun isActive(): Boolean {
        return !isViewed
    }
}