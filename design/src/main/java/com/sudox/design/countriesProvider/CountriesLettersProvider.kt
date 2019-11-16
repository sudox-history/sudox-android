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

        for (index in loadedCountries.size - 1 downTo 0) {
            val letter = loadedCountries[index].getName(context)[0].toString()

            if (index > 0) {
                val prevLetter = loadedCountries[index - 1].getName(context)[0].toString()

                if (letter != prevLetter) {
                    lettersPositions[index] = letter
                }
            } else {
                lettersPositions[index] = letter
            }
        }

        return lettersPositions
    }
}