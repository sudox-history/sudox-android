package ru.sudox.android.countries.impl

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sudox.android.core.ui.lists.letters.LetterViewObject
import ru.sudox.android.countries.api.CountriesFeatureApi
import ru.sudox.android.countries.impl.list.CountryViewObject
import ru.sudox.simplelists.model.BasicListItem
import java.util.*

/**
 * ViewModel фрагмента со списком стран.
 *
 * Реализует:
 * 1) Поиск стран
 * 2) Загрузку списка стран.
 *
 * @param countriesFeatureApi API модуля стран.
 */
class CountriesViewModel @ViewModelInject constructor(
    private val countriesFeatureApi: CountriesFeatureApi
) : ViewModel() {

    private var lastSearchRequest = ""
    val countriesLiveData = MutableLiveData<Pair<Boolean, MutableList<BasicListItem<*>>>>()

    /**
     * Загружает список стран.
     * Список приходит в LiveData.
     */
    fun load() = viewModelScope.launch(Dispatchers.IO) {
        if (countriesLiveData.value == null) {
            countriesLiveData.postValue(false to buildList(null))
            lastSearchRequest = ""
        }
    }

    /**
     * Ищет страны по указанному слову.
     * Список приходит в LiveData.
     *
     * @param request Искомая подстрока.
     */
    fun search(request: String) = viewModelScope.launch(Dispatchers.IO) {
        if (lastSearchRequest != request) {
            countriesLiveData.postValue(true to buildList(request))
            lastSearchRequest = request
        }
    }

    /**
     * Выдает флаг страны по её коду
     *
     * @param code Код страны
     * @return Drawable флага страны
     */
    fun getFlag(code: String) = countriesFeatureApi.getCountryFlag(code)

    /**
     * Выдает название страны по её коду
     *
     * @param code Код страны
     * @return Строка с названием страны
     */
    fun getName(code: String) = countriesFeatureApi.getCountryName(code)

    @Suppress("UNCHECKED_CAST")
    private fun buildList(text: String?): MutableList<BasicListItem<*>> {
        var items = countriesFeatureApi.getSupportedCountries().map {
            val code = countriesFeatureApi.getCountryCode(it)
            val item = BasicListItem(COUNTRY_VIEW_TYPE, CountryViewObject(it, code))
            item
        }.sortedBy {
            countriesFeatureApi.getCountryName(it.viewObject!!.regionCode)
        }

        if (text != null) {
            items = items.filter {
                countriesFeatureApi
                    .getCountryName(it.viewObject!!.regionCode)
                    .toLowerCase(Locale.getDefault())
                    .contains(text.toLowerCase(Locale.getDefault()))
            }
        }

        val completedList = items.toMutableList() as MutableList<BasicListItem<*>>
        val iterator = completedList.listIterator()
        var prevLetter: String? = null

        while (iterator.hasNext()) {
            val nextVO = iterator.next().viewObject as CountryViewObject
            val countryName = countriesFeatureApi.getCountryName(nextVO.regionCode)
            val letter = countryName.subSequence(0, 1).toString()

            if (prevLetter != letter) {
                iterator.previous()
                iterator.add(BasicListItem(COUNTRY_LETTER_VIEW_TYPE, LetterViewObject(letter)))
                prevLetter = letter
            }
        }

        return completedList
    }
}