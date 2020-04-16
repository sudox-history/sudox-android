package ru.sudox.android.managers.handlers.move

import android.view.View

class RightMoveHandler : MoveHandler {

    constructor() : super()
    constructor(removesFromViewOnPush: Boolean) : super(removesFromViewOnPush)
    constructor(duration: Long, removesFromViewOnPush: Boolean) : super(duration, removesFromViewOnPush)
    constructor(duration: Long) : super(duration)

    override fun onUpdate(from: View, to: View, progressAndAlpha: Float, targetTranslation: Float) {
        from.translationX = -targetTranslation * progressAndAlpha
        to.translationX = targetTranslation * (1 - progressAndAlpha)
    }
}