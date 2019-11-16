package com.sudox.design.countriesProvider

import android.content.Context
import androidx.core.content.edit
import com.sudox.design.BuildConfig
import com.sudox.design.R
import com.sudox.design.countriesProvider.entries.Country
import com.sudox.design.getLocale

internal const val PREFS_COUNTRIES = "countries"
internal const val PREF_APP_LANGUAGE = "app_lang"
internal const val PREF_APP_VERSION_CODE = "app_version_code"
internal const val PREF_COUNTRY_LETTERS_COUNT = "country_letters_count"
internal const val PREF_COUNTRY_LETTER_POSITION = "country_letter_position_"
internal const val PREF_COUNTRY_LETTER = "country_letter_"
internal const val PREF_REGION_CODE = "region_code_"

class CountriesProvider(val context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_COUNTRIES, Context.MODE_PRIVATE)

    private var loadedFromCache = false
    private var loadedCountries: List<Country>? = null
    private var loadedLetters: HashMap<Int, String>? = null
    private val lettersProvider = CountriesLettersProvider(this)

    fun tryLoadOrSort() = sharedPreferences.let {
        val cachedVersionCode = it.getInt(PREF_APP_VERSION_CODE, -1)
        val cachedAppLanguage = it.getString(PREF_APP_LANGUAGE, null)
        val cachedLettersCount = it.getInt(PREF_COUNTRY_LETTERS_COUNT, -1)

        if (cachedVersionCode != BuildConfig.VERSION_CODE ||
                cachedAppLanguage != context.resources.configuration.getLocale().displayLanguage ||
                cachedLettersCount == -1) {

            return sortAndCache()
        }

        loadedLetters = LinkedHashMap(cachedLettersCount, 1.0F)
        loadedCountries = MutableList(countries.size) { index ->
            val regionCode = it.getString("$PREF_REGION_CODE$index", null)
            val country = countries[regionCode] ?: return sortAndCache()

            country
        }

        for (index in 0 until cachedLettersCount) {
            val countryLetter = it.getString("$PREF_COUNTRY_LETTER$index", null) ?: return sortAndCache()
            val countryLetterPosition = it.getInt("$PREF_COUNTRY_LETTER_POSITION$index", -1)

            if (countryLetterPosition == -1) {
                return sortAndCache()
            }

            loadedLetters!![countryLetterPosition] = countryLetter
        }

        loadedFromCache = true
    }

    fun sortAndCache() = sharedPreferences.edit {
        clear()

        putString(PREF_APP_LANGUAGE, context.resources.configuration.getLocale().displayLanguage)
        putInt(PREF_APP_VERSION_CODE, BuildConfig.VERSION_CODE)

        loadedCountries = countries.values.sortedBy {
            it.getName(context)
        }.apply {
            forEachIndexed { index, country ->
                putString("$PREF_REGION_CODE$index", country.regionCode)
            }
        }

        loadedLetters = lettersProvider.getLetters(context, true)

        putInt(PREF_COUNTRY_LETTERS_COUNT, loadedLetters!!.size)

        loadedLetters!!.entries.forEachIndexed { index, pair ->
            putString("$PREF_COUNTRY_LETTER$index", pair.value)
            putInt("$PREF_COUNTRY_LETTER_POSITION$index", pair.key)
        }

        loadedFromCache = false
    }

    fun isLoadedFromCache(): Boolean {
        return loadedFromCache
    }

    fun getLettersProvider(): CountriesLettersProvider {
        return lettersProvider
    }

    fun getLoadedCountries(): List<Country> {
        return loadedCountries!!
    }

    fun getLoadedLetters(): HashMap<Int, String> {
        return loadedLetters!!
    }
}

@Suppress("MagicNumber")
val countries = hashMapOf(
        createSupportedCountryPair(Country("RU", R.string.russia, R.drawable.ic_flag_russia, 7)),
        createSupportedCountryPair(Country("CZ", R.string.czech_republic, R.drawable.ic_flag_czech_republic, 420)),
        createSupportedCountryPair(Country("DE", R.string.germany, R.drawable.ic_flag_germany, 49)),
        createSupportedCountryPair(Country("HU", R.string.hungary, R.drawable.ic_flag_hungary, 36)),
        createSupportedCountryPair(Country("IS", R.string.iceland, R.drawable.ic_flag_iceland, 354)),
        createSupportedCountryPair(Country("IT", R.string.italy, R.drawable.ic_flag_italy, 39)),
        createSupportedCountryPair(Country("LV", R.string.latvia, R.drawable.ic_flag_latvia, 371)),
        createSupportedCountryPair(Country("LT", R.string.lithuania, R.drawable.ic_flag_lithuania, 370)),
        createSupportedCountryPair(Country("NL", R.string.neatherlands, R.drawable.ic_flag_neatherlands, 31)),
        createSupportedCountryPair(Country("PL", R.string.poland, R.drawable.ic_flag_poland, 48)),
        createSupportedCountryPair(Country("SE", R.string.sweden, R.drawable.ic_flag_sweden, 46)),
        createSupportedCountryPair(Country("UA", R.string.ukraine, R.drawable.ic_flag_ukraine, 380))
)

private fun createSupportedCountryPair(country: Country): Pair<String, Country> {
    return country.regionCode to country
}