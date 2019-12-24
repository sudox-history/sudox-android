package com.sudox.messenger.android.moments

import android.graphics.drawable.Drawable

data class MomentVO(
        val userName: String,
        val userPhoto: Drawable,
        val isCreatedByMe: Boolean,
        val isViewed: Boolean
)