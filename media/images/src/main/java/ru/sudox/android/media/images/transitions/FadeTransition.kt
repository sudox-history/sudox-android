package ru.sudox.android.media.images.transitions

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.NoTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory

/**
 * Анимация плавного перехода.
 *
 * В отличии от стандартной работает только тогда,
 * когда картинка грузится с сервера.
 */
object FadeTransition : TransitionFactory<Drawable> {

    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return if (dataSource != DataSource.REMOTE) {
            NoTransition.get()
        } else {
            DrawableCrossFadeFactory
                    .Builder()
                    .build()
                    .build(dataSource, isFirstResource)
        }
    }
}