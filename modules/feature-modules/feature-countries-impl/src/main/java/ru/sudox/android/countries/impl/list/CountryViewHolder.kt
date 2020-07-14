package ru.sudox.android.countries.impl.list

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import ru.sudox.android.countries.impl.R
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для элемента страны
 *
 * @param view View, которая будет во ViewHolder
 * @param getCountryFlag Функция для получения флага страны
 * @param getCountryName Функция для получения названия страны
 * @param clickCallback Функция для обработки клика по стране
 */
class CountryViewHolder(
    view: View,
    private val getCountryName: (String) -> (String),
    private val getCountryFlag: (String) -> (Drawable),
    private val clickCallback: (CountryViewObject) -> (Unit)
) : BasicListHolder<CountryViewObject>(view) {

    private var vo: CountryViewObject? = null
    private var nameTextView = itemView.findViewById<TextView>(R.id.countryName)
    private var codeTextView = itemView.findViewById<TextView>(R.id.countryCode)

    init {
        view.setOnClickListener { clickCallback(vo!!) }
    }

    @SuppressLint("SetTextI18n")
    override fun bind(item: BasicListItem<CountryViewObject>, changePayload: List<Any>?) {
        vo = item.viewObject

        nameTextView.text = getCountryName(vo!!.regionCode)
        nameTextView.setCompoundDrawablesRelative(getCountryFlag(vo!!.regionCode), null, null, null)
        codeTextView.text = "+${vo!!.code}"
    }

    override fun cancelAnimations() {
        vo = null
    }
}