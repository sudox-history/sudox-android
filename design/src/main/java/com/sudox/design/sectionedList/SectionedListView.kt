package com.sudox.design.sectionedList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.R

class SectionedListView : RecyclerView {

    private var sectionNamePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var sectionNameMargin = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.sectionedListViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.SectionedListView, defStyleAttr, 0).use {
            val typefaceId = it.getResourceIdOrThrow(R.styleable.SectionedListView_sectionNameTypeface)

            sectionNameMargin = it.getDimensionPixelSize(R.styleable.SectionedListView_sectionNameMargin, 0)
            sectionNamePaint.color = it.getColorOrThrow(R.styleable.SectionedListView_sectionNameTextColor)
            sectionNamePaint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.SectionedListView_sectionNameTextSize).toFloat()
            sectionNamePaint.typeface = getFont(context, typefaceId)
        }

        addItemDecoration()
    }
}