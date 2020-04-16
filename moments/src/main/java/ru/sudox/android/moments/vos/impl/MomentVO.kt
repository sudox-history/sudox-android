package ru.sudox.android.moments.vos.impl

import android.content.Context
import ru.sudox.android.moments.vos.CircularUpdatableViewVO
import ru.sudox.android.moments.vos.NOT_SHOW_CONTENT_ON_STROKE_ANGLE
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

    override fun getContentOnCircleAngle(): Double {
        return NOT_SHOW_CONTENT_ON_STROKE_ANGLE
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