package com.sudox.design.sortedList.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.sortedList.SortedListView
import kotlin.math.max
import kotlin.math.min

class StickyLettersDecoration(
        val sortedListView: SortedListView,
        provider: StickyLettersProvider,
        context: Context
) : RecyclerView.ItemDecoration() {

    private var maxLetterWidth = 0

    private val letters = provider.getLetters(context)
    private val lettersPositions = letters.keys.sortedBy { -it }
    private val letterBounds = Rect()

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val letterTopPadding = sortedListView.lettersTopPadding
        val letterPaint = sortedListView.lettersPaint
        val letterMargin = sortedListView.lettersMargin

        var previousLetterY = Float.MAX_VALUE
        var previousLetterPosition = -1

        (parent.childCount - 1 downTo 0).map {
            parent.getChildAt(it)
        }.filter {
            !isChildNotVisible(it, parent)
        }.forEach {
            val position = parent.getChildAdapterPosition(it)

            if (position < RecyclerView.NO_POSITION) {
                return@forEach
            }

            val letter = letters[position] ?: return@forEach
            val letterY = (it.top - letterTopPadding)
                    .coerceAtLeast(letterTopPadding)
                    .coerceAtMost((previousLetterY - letterTopPadding).toInt())
                    .toFloat()

            canvas.drawText(letter, 0F, letterY, letterPaint)

            previousLetterY = letterY
            previousLetterPosition = position
        }

        previousLetterPosition = if (previousLetterPosition == RecyclerView.NO_POSITION) {
            parent.getChildAdapterPosition(parent.getChildAt(0)) + 1
        } else {
            previousLetterPosition
        }

        val stickyLetterPosition = lettersPositions.find { it < previousLetterPosition } ?: return
        val stickyLetter = letters[stickyLetterPosition]!!

        letterPaint.getTextBounds(stickyLetter, 0, stickyLetter.length, letterBounds)

        val stickyLetterTop = previousLetterY - letterBounds.height() - letterMargin
        val stickyLetterY = min(stickyLetterTop, letterTopPadding.toFloat())

        canvas.drawText(stickyLetter, 0F, stickyLetterY, letterPaint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val letterTopPadding = sortedListView.lettersTopPadding
        val letterRightPadding = sortedListView.lettersRightPadding
        val letterPaint = sortedListView.lettersPaint

        val position = parent.getChildAdapterPosition(view)
        val letter = letters[position]

        if (letter != null) {
            letterPaint.getTextBounds(letter, 0, letter.length, letterBounds)
            maxLetterWidth = max(letterBounds.width(), maxLetterWidth)
            outRect.top += letterBounds.height() + letterTopPadding
        }

        outRect.left += letterBounds.height() + letterRightPadding
    }

    private fun isChildNotVisible(child: View, parent: RecyclerView): Boolean {
        val letterTopPadding = sortedListView.lettersTopPadding

        return child.top - letterTopPadding - letterBounds.height() < 0 ||
                child.top + letterTopPadding + letterBounds.height() > parent.height
    }
}