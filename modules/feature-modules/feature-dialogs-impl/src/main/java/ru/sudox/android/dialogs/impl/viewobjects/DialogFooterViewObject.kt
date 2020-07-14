package ru.sudox.android.dialogs.impl.viewobjects

import androidx.annotation.PluralsRes
import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для футера списка диалогов.
 *
 * @param count Количество диалогов.
 * @param pluralId ID строки с названием типа записей
 */
data class DialogFooterViewObject(
    val count: Int,
    @PluralsRes val pluralId: Int
) : BasicListViewObject<Int, DialogFooterViewObject> {
    override fun getId(): Int = Int.MAX_VALUE
}