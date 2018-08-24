package com.sudox.android.common.transitions

import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet

class ChatTransition : TransitionSet() {
    init {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds())
        addTransition(ChangeTransform())
    }
}