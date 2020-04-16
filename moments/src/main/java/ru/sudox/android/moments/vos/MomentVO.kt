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
        override var userId: Long,
        override var userName: String,
        override var photoId: Long,
        val publishTime: Long,
        val isViewed: Boolean
) : CircularUpdatableViewVO, PeopleVO {

    override var seenTime: Long = 0

    override fun canShowIndicator(): Boolean {
        return false
    }

    override fun getTitle(context: Context): String? {
        return userName
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
//        (view as AvatarImageView).vo = null
    }

    override fun bindViewInCenter(view: View) {
//        (view as AvatarImageView).let {
//            it.layoutParams = ViewGroup.LayoutParams(
//                    view.context.resources.getDimensionPixelSize(R.dimen.momentvo_photo_width),
//                    view.context.resources.getDimensionPixelSize(R.dimen.momentvo_photo_height)
//            )
//
//            it.vo = this
//        }
    }

    override fun getViewInCenter(context: Context): View {
        return AvatarImageView(context)
    }

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return null
    }

    override fun getStatusMessage(context: Context): String? {
        return null
    }

    override fun isStatusAboutOnline(): Boolean {
        return false
    }

    override fun isStatusActive(): Boolean {
        return false
    }

    override fun isActive(): Boolean {
        return !isViewed
    }
}