package com.sudox.design.nicknameEditText

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import com.sudox.design.editTextLayout.ChildEditText

internal const val TAG_SPLITTER = '#'

class NicknameEditText : ChildEditText {

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

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as NicknameEditTextState

        state.apply {
            super.onRestoreInstanceState(state.superState)
            state.readToView(this@NicknameEditText)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return NicknameEditTextState(superState!!).apply {
            writeFromView(this@NicknameEditText)
        }
    }

    fun setNicknameTag(tag: String?) {
        this.tag = "${TAG_SPLITTER}$tag"
        this.tagColorSpannable = ForegroundColorSpan(tagColor)

        paint.getTextBounds(this.tag!!, 0, this.tag!!.length, tagBounds)
    }

    fun getNicknameTag(): String? {
        return tag?.removePrefix(TAG_SPLITTER.toString()) ?: return null
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
        if (tag == null) {
            return null
        }

        return text?.toString()?.removeSuffix(tag!!) ?: return null
    }
}