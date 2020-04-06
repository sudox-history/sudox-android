package ru.sudox.android.people.peopletab.vos

import android.content.Context
import ru.sudox.android.people.common.vos.CLOSE_BUTTON
import ru.sudox.android.people.common.vos.PeopleVO
import ru.sudox.android.people.peopletab.R

/**
 * ViewObject для возможно знакомого человека.
 * Информацию по другим полям смотрите в классе PeopleVO
 *
 * @param mutualCount Количество знакомых друзей.
 */
class MaybeYouKnowVO(
        override var userId: Long,
        override var userName: String,
        override var seenTime: Long,
        override var photoId: Long,
        val mutualCount: Int
) : PeopleVO {

    override fun getButtons(): Array<Triple<Int, Int, Int>>? {
        return CLOSE_BUTTON
    }

    override fun getStatusMessage(context: Context): String {
        return context.resources.getQuantityString(R.plurals.mutual, mutualCount, mutualCount)
    }

    override fun isStatusAboutOnline(): Boolean {
        return false
    }

    override fun isStatusActive(): Boolean {
        return false
    }
}