package ru.sudox.android.people

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.android.media.images.views.avatar.AvatarImageView
import ru.sudox.android.media.images.views.avatar.AvatarVO

class ProfileController : ScrollableController() {

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return AvatarImageView(activity!!).apply {
            layoutParams = ViewGroup.LayoutParams(120, 120)

            setVO(object : AvatarVO {
                override fun getResourceId(): Long {
                    return 1L
                }

                override fun canShowIndicator(): Boolean {
                    return true
                }

                override fun getNumberInIndicator(): Int {
                    return 10
                }
            }, glide)
        }
    }
}
