package ru.sudox.android.injector

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.sudox.android.dialogs.api.DialogsFeatureApi
import ru.sudox.android.dialogs.impl.DialogsFeatureImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DialogsFeatureModule {

    @Provides
    @Singleton
    fun bindApi(): DialogsFeatureApi = DialogsFeatureImpl()
}