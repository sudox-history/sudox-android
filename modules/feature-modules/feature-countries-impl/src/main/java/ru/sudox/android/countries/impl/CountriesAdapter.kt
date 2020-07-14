package ru.sudox.android.countries.impl

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.sudox.android.core.ui.lists.ScreenListAdapter
import ru.sudox.android.core.ui.lists.letters.createLetterViewHolder
import ru.sudox.android.countries.impl.list.CountryViewHolder
import ru.sudox.android.countries.impl.list.CountryViewObject
import ru.sudox.simplelists.BasicListHolder

const val COUNTRY_LETTER_VIEW_TYPE = 0
const val COUNTRY_VIEW_TYPE = 1

/**
 * Провайдер для списка стран
 *
 * @param getCountryFlag Функция для получения флага страны
 * @param getCountryName Функция для получения названия страны
 * @param clickCallback Функция для обработки клика по стране
 */
class CountriesAdapter(
    private val getCountryName: (String) -> (String),
    private val getCountryFlag: (String) -> (Drawable),
    private val clickCallback: (CountryViewObject) -> (Unit)
) : ScreenListAdapter() {

    override fun createItemViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> = if (viewType == COUNTRY_LETTER_VIEW_TYPE) {
        createLetterViewHolder(inflater, parent)
    } else {
        CountryViewHolder(inflater.inflate(R.layout.item_country, parent, false), getCountryName, getCountryFlag, clickCallback)
    }
}