package ru.sudox.android.countries

import android.content.Context
import androidx.lifecycle.MutableLiveData
import ru.sudox.android.core.CoreViewModel
import ru.sudox.android.countries.helpers.COUNTRIES
import ru.sudox.android.countries.vos.CountryVO
import javax.inject.Inject

class CountrySelectViewModel @Inject constructor() : CoreViewModel() {

    val searchLiveData = MutableLiveData<List<CountryVO>>()
    val countriesLiveData = MutableLiveData<List<CountryVO>>()

    fun loadCountries(context: Context, resetSearch: Boolean) {
        if (resetSearch) {
            searchLiveData.value = null
        }

        if (searchLiveData.value == null) {
            countriesLiveData.postValue(COUNTRIES.values.sortedBy {
                it.getName(context)
            })
        }
    }

    fun searchStartsWith(context: Context, text: String) {
        searchLiveData.postValue(COUNTRIES.values.filter {
            it.getName(context).startsWith(text, ignoreCase = true)
        }.sortedBy {
            it.getName(context)
        })
    }
}