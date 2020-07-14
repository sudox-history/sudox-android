package ru.sudox.android.injector

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.sudox.android.main.api.MainFeatureApi
import ru.sudox.android.main.impl.MainFeatureImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MainFeatureModule {

    @Provides
    @Singleton
    fun provideApi(): MainFeatureApi = MainFeatureImpl()
}