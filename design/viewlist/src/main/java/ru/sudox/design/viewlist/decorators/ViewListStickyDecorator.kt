package ru.sudox.design.viewlist.decorators

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import kotlin.math.max
import kotlin.math.min

class ViewListStickyDecorator(
        val list: ViewList
) : RecyclerView.ItemDecoration() {

    private val letterBounds = Rect()
    private var letterMaxWidth = 0

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val letterX = list.initialPaddingLeft.toFloat()
        val letters = (list.adapter as? ViewListAdapter<*>)?.stickyLetters ?: return
        val lettersPositions = letters.keys
        var prevLetterY = Int.MAX_VALUE
        var prevLetterPosition = -1
        var prevDiffToCenter = 0

        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)

            if (child.top - list.letterPaddingTop - letterBounds.height() !in parent.paddingTop .. parent.height) {
                continue
            }

            val position = parent.getChildAdapterPosition(child)

            if (position < RecyclerView.NO_POSITION) {
                continue
            }

            val letter = letters[position] ?: continue
            val letterY = min(max(max(child.top - list.letterPaddingTop, list.letterPaddingTop), parent.paddingTop + letterBounds.height()), prevLetterY - list.letterPaddingTop)

            canvas.drawText(letter, letterX, letterY.toFloat(), list.letterPaint)

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
        val stickyLetter = letters[stickyLetterPosition] ?: return

        list.letterPaint.getTextBounds(stickyLetter, 0, stickyLetter.length, letterBounds)

        val stickyLetterBottom = prevLetterY - letterBounds.height() - prevDiffToCenter + letterBounds.height() / 2
        val stickyLetterY = min(max(min(stickyLetterBottom, list.letterPaddingTop), parent.paddingTop + letterBounds.height()), stickyLetterBottom)

        canvas.drawText(stickyLetter, letterX, stickyLetterY.toFloat(), list.letterPaint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val letters = (list.adapter as? ViewListAdapter<*>)?.stickyLetters ?: return
        val letter = letters[position]

        if (letter != null) {
            list.letterPaint.getTextBounds(letter, 0, letter.length, letterBounds)
            letterMaxWidth = max(letterBounds.width(), letterMaxWidth)
            outRect.top += letterBounds.height() + list.letterPaddingTop
        }

        view.updatePadding(left = list.initialPaddingLeft + letterMaxWidth + list.letterPaddingRight)
    }
}