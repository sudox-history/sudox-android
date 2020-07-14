package ru.sudox.android.countries.impl.list

import ru.sudox.android.core.ui.lists.letters.LetterViewBinder
import ru.sudox.simplelists.model.BasicListViewObject

/**
 * View, для получения липкой буквы на основе VO страны
 *
 * @param getCountryName Функция для получения названия страны
 */
class CountryLetterBinder(
    private val getCountryName: (String) -> (String)
) : LetterViewBinder {

    override fun getStringForLetter(vo: BasicListViewObject<*, *>?): String? {
        if (vo is CountryViewObject) {
            return getCountryName(vo.regionCode)
        }

        return null
    }
}