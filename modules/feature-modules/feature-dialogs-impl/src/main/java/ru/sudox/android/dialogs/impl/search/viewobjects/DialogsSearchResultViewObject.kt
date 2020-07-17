package ru.sudox.android.dialogs.impl.search.viewobjects

import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewObject результатов поиска.
 *
 * @param chatsItems Найденные записи чатов.
 * @param messagesItems Найденные записи сообщений.
 */
data class DialogsSearchResultViewObject(
    val chatsItems: List<BasicListItem<*>>,
    val messagesItems: List<BasicListItem<*>>
)