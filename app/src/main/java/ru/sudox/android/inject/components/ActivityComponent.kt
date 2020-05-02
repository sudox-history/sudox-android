package ru.sudox.android.inject.components

import androidx.lifecycle.ViewModelProvider
import ru.sudox.android.AppActivity
import ru.sudox.android.core.inject.CoreActivityComponent
import ru.sudox.android.core.inject.CoreActivityModule
import ru.sudox.android.core.inject.scopes.CoreActivityScope
import dagger.Subcomponent

@CoreActivityScope
@Subcomponent(modules = [CoreActivityModule::class])
interface ActivityComponent : CoreActivityComponent {
    fun inject(appActivity: AppActivity)
    fun viewModelFactory(): ViewModelProvider.Factory
}