package ru.sudox.android.countries.inject

import ru.sudox.android.countries.views.PhoneEditText

interface CountriesComponent {
    fun inject(phoneEditText: PhoneEditText)
}