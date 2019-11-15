package com.sudox.design.countriesProvider

import android.content.Context
import com.sudox.design.sortedList.decorations.StickyLettersProvider

class CountriesLettersProvider(val provider: CountriesProvider) : StickyLettersProvider {

    override fun getLetters(context: Context, ignoreCache: Boolean): HashMap<Int, String> {
        if (!ignoreCache) {
            return provider.getLoadedLetters()
        }

        val lettersPositions = LinkedHashMap<Int, String>()
        val loadedCountries = provider.getLoadedCountries()
        var previousLetter: String? = null

        loadedCountries.map {
            it.getName(context)[0].toString()
        }.forEachIndexed { index, letter ->
            if (previousLetter != letter) {
                lettersPositions[index] = letter
                previousLetter = letter
            }
        }

        return lettersPositions
    }
}