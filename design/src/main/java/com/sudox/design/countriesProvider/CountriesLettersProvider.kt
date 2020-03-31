package com.sudox.design.countriesProvider

import android.content.Context
import com.sudox.design.lists.sortedList.decorations.StickyLettersProvider

class CountriesLettersProvider : StickyLettersProvider {

    override fun getLetters(context: Context): HashMap<Int, String> {
        val lettersPositions = LinkedHashMap<Int, String>()
        val loadedCountries = getCountries(context)

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