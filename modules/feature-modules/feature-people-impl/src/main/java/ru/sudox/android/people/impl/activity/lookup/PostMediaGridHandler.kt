package ru.sudox.android.people.impl.activity.lookup

import android.content.Context
import android.util.Size
import ru.sudox.android.core.ui.mityushkinlayout.MityushkinLayoutHandler
import ru.sudox.android.people.impl.R

/**
 * Обработчик сетки для медиа-вложений
 *
 * @param context Контекст приложения/активности
 * @param getItemsCount Функция для получения количества элементов сетки.
 */
class PostMediaGridHandler(
    context: Context,
    private val getItemsCount: () -> (Int)
) : MityushkinLayoutHandler() {

    private val singleElementMaxSize = Size(
        context.resources.getDimensionPixelSize(R.dimen.postMediaGridSingleViewMaxWidth),
        context.resources.getDimensionPixelSize(R.dimen.postMediaGridSingleViewMaxHeight)
    )

    private val singleElementMinSize = Size(
        context.resources.getDimensionPixelSize(R.dimen.postMediaGridSingleViewMinWidth),
        context.resources.getDimensionPixelSize(R.dimen.postMediaGridSingleViewMinHeight)
    )

    private val twoElementsSize = Size(-1, context.resources.getDimensionPixelSize(R.dimen.postMediaGridTwoViewsHeight))
    private val threeAndMoreElementsSize =
        Size(-1, context.resources.getDimensionPixelSize(R.dimen.postMediaGridThreeAndMoreViewsHeight))

    override fun getSpanSize(position: Int): Int {
        val itemsCount = getItemsCount()

        return if (itemsCount == 1 || (itemsCount in 3..4 && position == 0)) {
            12
        } else if (itemsCount in 2..3 || (position in 0..1 && (itemsCount in 5..6 || itemsCount in 8..9))) {
            6
        } else if (itemsCount in 4..5 || itemsCount in 7..8 || (itemsCount == 9 && position in 2..4) || (itemsCount == 10 && position in 0..5)) {
            4
        } else {
            3
        }
    }

    override fun getMaximumSize(position: Int): Size = if (getItemsCount() > 1) {
        getMinimumSize(position)
    } else {
        singleElementMaxSize
    }

    override fun getMinimumSize(position: Int): Size = when (getItemsCount()) {
        1 -> singleElementMinSize
        2 -> twoElementsSize
        else -> threeAndMoreElementsSize
    }

    override fun getCellsCount(): Int = 12
}