package com.sudox.design.nicknameEditText

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.use
import com.sudox.design.R

internal const val TAG_SPLITTER = '#'

class NicknameEditText : AppCompatEditText {

    private var tagColor = 0

    internal var tag: String? = null
    internal var tagBounds = Rect()
    internal var tagColorSpannable: ForegroundColorSpan? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.nicknameEditTextStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.NicknameEditText, defStyleAttr, 0).use {
            tagColor = it.getColorOrThrow(R.styleable.NicknameEditText_tagTextColor)
        }
    }

    init {
        isSingleLine = true
        imeOptions = EditorInfo.IME_ACTION_DONE
        maxLines = 1

        addTextChangedListener(NicknameTextWatcher(this))
    }

    fun setTag(tag: String?) {
        this.tag = "${TAG_SPLITTER}$tag"
        this.tagColorSpannable = ForegroundColorSpan(tagColor)

        paint.getTextBounds(this.tag!!, 0, this.tag!!.length, tagBounds)
    }

    fun scrollToEnd() {
        // Spannables not calculating
        val totalTextWidth = paint.measureText(text.toString() + tag).toInt()
        val width = right - left

        if (totalTextWidth < width) {
            return
        }

        val needScroll = totalTextWidth - width
        scrollTo(needScroll, scrollY)
    }

    fun getNickname(): String? {
        return text?.toString()?.removeSuffix(tag!!) ?: return null
    }
}