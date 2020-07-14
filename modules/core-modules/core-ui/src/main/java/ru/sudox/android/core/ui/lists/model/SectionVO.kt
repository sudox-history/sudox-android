package ru.sudox.android.core.ui.lists.model

import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import ru.sudox.simplelists.model.BasicListViewObject

internal const val SECTION_TYPE_CHANGED_FLAG = 1

/**
 * ViewObject для секции.
 *
 * @param titleRes ID строки заголовка
 * @param typesMenuRes ID меню типов
 * @param sortsMenuRes ID меню сортировок
 * @param defaultTypeId ID типа по-умолчанию
 * @param defaultSortId ID сортировки по-умолчанию
 * @param canCollapse Можно ли свернуть секцию?
 * @param countValue Значение счетчика.
 */
data class SectionVO(
    @StringRes val titleRes: Int = 0,
    @MenuRes val typesMenuRes: Int = 0,
    @MenuRes val sortsMenuRes: Int = 0,
    @IdRes val defaultTypeId: Int = 0,
    @IdRes val defaultSortId: Int = 0,
    val canCollapse: Boolean = false,
    val countValue: Int = 0
) : BasicListViewObject<Int, SectionVO> {
    var isCollapsed = false
    var selectedTypeId = defaultTypeId
    var selectedSortId = defaultSortId
    var order = 0

    override fun getId(): Int = order
}