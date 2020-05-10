package ru.sudox.android.media.texts

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.util.LinkifyCompat
import androidx.core.widget.addTextChangedListener
import ru.sudox.android.media.texts.spans.CustomURLSpan
import kotlin.math.ceil
import kotlin.math.max

val HASH_TAG_REGEX = """#[\w\\=-\\._:]{0,20}""".toPattern()
val MENTION_REGEX = """@[\w\\=-\\._:]{0,20}""".toPattern()

class LinkifiedTextView : AppCompatTextView {

    private var blockLinkifying = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(afterTextChanged = {
            if (!blockLinkifying) {
                blockLinkifying = true

                LinkifyCompat.addLinks(this, Linkify.ALL)
                LinkifyCompat.addLinks(this, HASH_TAG_REGEX, null)
                LinkifyCompat.addLinks(this, MENTION_REGEX, null)

                blockLinkifying = false

                if (text is SpannableStringBuilder) {
                    val spannable = text as SpannableStringBuilder
                    val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)

                    spans.forEach {
                        val span = CustomURLSpan(it.url)
                        val start = spannable.getSpanStart(it)
                        val end = spannable.getSpanEnd(it)

                        spannable.removeSpan(it)
                        spannable.setSpan(span, start, end, 0)
                    }
                }
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            var maxWidth = 0F

            for (i in 0 until layout.lineCount) {
                maxWidth = max(maxWidth, layout.getLineWidth(i))
            }

            setMeasuredDimension(ceil(maxWidth).toInt() + paddingLeft + paddingRight, measuredHeight)
        }
    }
}