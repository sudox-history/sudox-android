package ru.sudox.simplelists.helpers

import ru.sudox.simplelists.model.BasicListItem
import ru.sudox.simplelists.model.BasicListViewObject
import java.lang.ClassCastException

/**
 * Ищет различия в обьекте и возвращает их флаги.
 *
 * @param oldItem Старый элемент
 * @param newItem Новый элемент
 * @return Список с флагами различий.
 */
@Suppress("UNCHECKED_CAST")
fun getChangePayload(oldItem: BasicListItem<*>, newItem: BasicListItem<*>): List<Any>? {
    val oldVO = oldItem.viewObject ?: return null
    val newVO = newItem.viewObject as? BasicListViewObject<*, Any?> ?: return null

    return try {
        newVO.getChangePayload(oldVO)
    } catch (e: ClassCastException) {
        null
    }
}