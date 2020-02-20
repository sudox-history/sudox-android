package com.sudox.messenger.android.moments.vos

import android.graphics.drawable.Drawable

data class MomentVO(
        val isStartViewed: Boolean,
        val isFullyViewed: Boolean,
        val publisherName: String,
        val publisherPhoto: Drawable,
        val publishTime: Long
)