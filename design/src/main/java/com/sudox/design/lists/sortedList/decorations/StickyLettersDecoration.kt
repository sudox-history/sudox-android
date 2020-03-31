package com.sudox.design.lists.sortedList.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.sortedList.SortedListView
import kotlin.math.max
import kotlin.math.min

class StickyLettersDecoration(
        val sortedListView: SortedListView,
        provider: StickyLettersProvider,
        context: Context
) : RecyclerView.ItemDecoration() {

    private val letters = provider.getLetters(context)
    private val lettersPositions = letters.keys
    private val letterBounds = Rect()
    private var letterMaxWidth = 0

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) = sortedListView.let { list ->
        val letterX = parent.paddingStart.toFloat()
        var prevLetterY = Int.MAX_VALUE
        var prevLetterPosition = -1
        var prevDiffToCenter = 0

        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)

            if (child.top - list.lettersTopPadding - letterBounds.height() !in parent.paddingTop .. parent.height) {
                continue
            }

            val position = parent.getChildAdapterPosition(child)

            if (position < RecyclerView.NO_POSITION) {
                continue
            }

            val letter = letters[position] ?: continue
            val letterY = min(max(max(child.top - list.lettersTopPadding, list.lettersTopPadding), parent.paddingTop + letterBounds.height()), prevLetterY - list.lettersTopPadding)

            canvas.drawText(letter, letterX, letterY.toFloat(), list.lettersPaint)

            prevLetterY = letterY
            prevLetterPosition = position
            prevDiffToCenter = child.height / 2
        }

        prevLetterPosition = if (prevLetterPosition == RecyclerView.NO_POSITION) {
            parent.getChildAdapterPosition(parent.getChildAt(0)) + 1
        } else {
            prevLetterPosition
        }

        val stickyLetterPosition = lettersPositions.find { it < prevLetterPosition } ?: return
        val stickyLetter = letters[stickyLetterPosition]!!

        list.lettersPaint.getTextBounds(stickyLetter, 0, stickyLetter.length, letterBounds)

        val stickyLetterBottom = prevLetterY - letterBounds.height() - prevDiffToCenter + letterBounds.height() / 2
        val stickyLetterY = min(max(min(stickyLetterBottom, list.lettersTopPadding), parent.paddingTop + letterBounds.height()), stickyLetterBottom)

        canvas.drawText(stickyLetter, letterX, stickyLetterY.toFloat(), list.lettersPaint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) = sortedListView.let { list ->
        val position = parent.getChildAdapterPosition(view)
        val letter = letters[position]

        if (letter != null) {
            list.lettersPaint.getTextBounds(letter, 0, letter.length, letterBounds)
            letterMaxWidth = max(letterBounds.width(), letterMaxWidth)
            outRect.top += letterBounds.height() + list.lettersTopPadding
        }

        view.updatePadding(left = letterMaxWidth + list.lettersRightPadding)
    }
}