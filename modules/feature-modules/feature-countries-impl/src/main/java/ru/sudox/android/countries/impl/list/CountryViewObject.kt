package ru.sudox.android.countries.impl.list

import ru.sudox.simplelists.model.BasicListViewObject

/**
 * ViewObject для страны
 *
 * @param regionCode Код региона страны
 * @param code Телефонный код страны
 */
class CountryViewObject(
    val regionCode: String,
    val code: Int
) : BasicListViewObject<String, CountryViewObject> {
    override fun getId(): String = regionCode
}