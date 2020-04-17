package ru.sudox.android.countries.inject

import ru.sudox.android.countries.views.phoneedittext.PhoneEditText

interface CountriesComponent {
    fun inject(phoneEditText: PhoneEditText)
}