package ru.sudox.android.countries.api

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import java.util.*

/**
 * API модуля функционала стран.
 */
interface CountriesFeatureApi {

    /**
     * Выдает локаль, в которую переведены названия стран
     */
    fun getCountryNamesLocale(): Locale

    /**
     * Выдает флаг страны по её коду
     *
     * @param code Код страны
     * @return Drawable флага страны
     */
    fun getCountryFlag(code: String): Drawable

    /**
     * Выдает название страны по её коду
     *
     * @param code Код страны
     * @return Строка с названием страны
     */
    fun getCountryName(code: String): String

    /**
     * Выдает телефонный код страны по её коду
     *
     * @param code Код страны
     * @return Телефонный код страны
     */
    fun getCountryCode(code: String): Int

    /**
     * Выдает список с кодами регионов поддерживаемых стран
     *
     * @return Список с кодами регионов поддерживаемых стран
     */
    fun getSupportedCountries(): List<String>

    /**
     * Выдает фрагмент для запуска функции выбора страны.
     *
     * @return Фрагмент со списком стран.
     */
    fun getStartupFragment(): Fragment
}