package ru.sudox.android.core.ui.lists.dependencies

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для элемента тестовой секции
 *
 * @param title Название элемента
 * @param requestedId ID элемента
 */
class SectionedScreenListItemViewObject(
    val title: String,
    val requestedId: String? = null
) : BasicListViewObject<String, SectionedScreenListItemViewObject> {
    override fun getId(): String = requestedId ?: title
}