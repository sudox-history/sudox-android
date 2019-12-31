package com.sudox.design.lists.sectionedList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import com.sudox.design.R
import com.sudox.design.lists.BasicRecyclerView

class SectionedListView : BasicRecyclerView {

    internal var sectionNameTopPadding = 0
    internal var sectionNameBottomPadding = 0
    internal var sectionNamePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.sectionedListViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.SectionedListView, defStyleAttr, 0).use {
            val typefaceId = it.getResourceIdOrThrow(R.styleable.SectionedListView_sectionNameTypeface)

            sectionNameTopPadding = it.getDimensionPixelSize(R.styleable.SectionedListView_sectionNameTopPadding, 0)
            sectionNameBottomPadding = it.getDimensionPixelSize(R.styleable.SectionedListView_sectionNameBottomPadding, 0)
            sectionNamePaint.color = it.getColorOrThrow(R.styleable.SectionedListView_sectionNameTextColor)
            sectionNamePaint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.SectionedListView_sectionNameTextSize).toFloat()
            sectionNamePaint.typeface = getFont(context, typefaceId)
        }

        addItemDecoration(SectionedListDecorator(this))
    }
}