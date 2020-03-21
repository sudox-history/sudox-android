package com.sudox.messenger.android.media.texts.spans

import android.text.TextPaint
import android.text.style.URLSpan

class CustomURLSpan(url: String?) : URLSpan(url) {

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.let {
            it.isUnderlineText = false
            it.color = it.linkColor
        }
    }
}