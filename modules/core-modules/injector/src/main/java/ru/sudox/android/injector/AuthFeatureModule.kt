package ru.sudox.android.injector

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.sudox.android.auth.api.AuthFeatureApi
import ru.sudox.android.auth.impl.AuthFeatureImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AuthFeatureModule {

    @Provides
    @Singleton
    fun provideApi(): AuthFeatureApi = AuthFeatureImpl()
}