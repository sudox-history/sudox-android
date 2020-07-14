package ru.sudox.android.countries.impl

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import com.google.i18n.phonenumbers.PhoneNumberUtil
import ru.sudox.android.countries.api.CountriesFeatureApi
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Реализация функционала модуля стран.
 *
 * @param assetManager Менеджер ресурсов.
 * @param phoneNumberUtil PhoneNumberUtil для работы с номерными базами
 */
class CountriesFeatureImpl @Inject constructor(
    private val resources: Resources,
    private val assetManager: AssetManager,
    private val phoneNumberUtil: PhoneNumberUtil
) : CountriesFeatureApi {

    private var localeHash = 0
    private val flagsCache = HashMap<String, Drawable>()
    private val namesCache = HashMap<String, String>()
    private val codesCache = HashMap<String, Int>()

    override fun getCountryNamesLocale(): Locale {
        return if (!resources.getBoolean(R.bool.use_english_countries_names)) {
            ConfigurationCompat.getLocales(resources.configuration)[0]
        } else {
            Locale.ENGLISH
        }
    }

    override fun getCountryName(code: String): String {
        val currentLocale = getCountryNamesLocale()
        val currentLocaleHash = currentLocale.hashCode()

        if (localeHash != currentLocaleHash) {
            localeHash = currentLocaleHash
            namesCache.clear()
        }

        return namesCache.getOrPut(code) {
            Locale("", code).getDisplayName(currentLocale)
        }
    }

    override fun getCountryCode(code: String): Int = codesCache.getOrPut(code) {
        phoneNumberUtil.getCountryCodeForRegion(code)
    }

    override fun getCountryFlag(code: String): Drawable = flagsCache.getOrPut(code, {
        val firstChar = code[0].toInt() + 127397
        val secondChar = code[1].toInt() + 127397
        val emoji = String(intArrayOf(firstChar, secondChar), 0, 2)
        val firstPart = emoji.codePointAt(0).toString(16)
        val secondPart = emoji.codePointAt(2).toString(16)
        val drawableStream = assetManager.open("flags/$firstPart-$secondPart.png")

        drawableStream.use {
            Drawable.createFromStream(it, null)
        }
    })

    override fun getSupportedCountries(): List<String> = phoneNumberUtil.supportedRegions.toList()
    override fun getStartupFragment(): Fragment = CountriesFragment()
}