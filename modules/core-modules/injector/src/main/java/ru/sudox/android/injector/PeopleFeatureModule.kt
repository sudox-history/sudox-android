package ru.sudox.android.injector

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.sudox.android.people.api.PeopleFeatureApi
import ru.sudox.android.people.impl.PeopleFeatureImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PeopleFeatureModule {

    @Provides
    @Singleton
    fun provideApi(): PeopleFeatureApi = PeopleFeatureImpl()
}