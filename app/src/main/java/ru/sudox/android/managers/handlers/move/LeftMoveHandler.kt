package ru.sudox.android.managers.handlers.move

import android.view.View
import ru.sudox.android.managers.handlers.move.MoveHandler

class LeftMoveHandler : MoveHandler {

    constructor() : super()
    constructor(removesFromViewOnPush: Boolean) : super(removesFromViewOnPush)
    constructor(duration: Long, removesFromViewOnPush: Boolean) : super(duration, removesFromViewOnPush)
    constructor(duration: Long) : super(duration)

    override fun onUpdate(from: View, to: View, progressAndAlpha: Float) {
        from.translationX = from.measuredWidth * progressAndAlpha
        to.translationX = -to.measuredWidth * (1 - progressAndAlpha)
    }
}