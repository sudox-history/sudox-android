package com.sudox.messenger.android.auth.country

import android.content.Context
import com.sudox.design.sortedList.decorations.StickyLettersProvider
import com.sudox.design.common.entries.Country

class AuthCountryLettersProvider(
        val countries: List<Country>
) : StickyLettersProvider {

    override fun getLetters(context: Context): HashMap<Int, String> {
        val lettersPositions = HashMap<Int, String>()
        var previousLetter: String? = null

        countries.map {
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