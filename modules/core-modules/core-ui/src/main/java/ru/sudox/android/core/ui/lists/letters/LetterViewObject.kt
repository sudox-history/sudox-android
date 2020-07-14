package ru.sudox.android.core.ui.lists.letters

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для липкой буквы
 * В качестве ID используется строка с буквой
 *
 * @param char Строка с буквой (ну или текстом)
 */
class LetterViewObject(
    val char: String
) : BasicListViewObject<String, LetterViewObject> {
    override fun getId(): String = char
}