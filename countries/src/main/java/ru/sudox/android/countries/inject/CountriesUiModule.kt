package ru.sudox.android.countries.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.sudox.android.core.inject.viewmodel.ViewModelKey
import ru.sudox.android.countries.CountrySelectViewModel

@Module
abstract class CountriesUiModule {

    @Binds
    @IntoMap
    @ViewModelKey(CountrySelectViewModel::class)
    internal abstract fun countrySelectViewModel(viewModel: CountrySelectViewModel): ViewModel
}