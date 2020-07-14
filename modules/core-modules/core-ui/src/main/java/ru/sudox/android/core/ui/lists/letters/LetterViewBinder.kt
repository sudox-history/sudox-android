package ru.sudox.android.core.ui.lists.letters

import android.widget.TextView
import ru.sudox.simplelists.model.BasicListViewObject
import ru.sudox.simplelists.sticky.StickyViewBinder

/**
 * Класс-обертка для получения буквы для липкой View
 */
interface LetterViewBinder : StickyViewBinder<TextView> {

    override fun bindData(view: TextView, vo: BasicListViewObject<*, *>?): Boolean {
        val string = getStringForLetter(vo)

        if (string != null) {
            val letter = string[0]

            if (view.text.isEmpty() || view.text[0] != letter) {
                view.text = letter.toString()
            }

            return true
        }

        return false
    }

    /**
     * Выдает строку на основе которой нужно получить липкую букву
     *
     * @param vo ViewObject, из которой нужно получить строку
     * @return Строка из которой нужно получить букву (null если нет информации)
     */
    fun getStringForLetter(vo: BasicListViewObject<*, *>?): String?
}