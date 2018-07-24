package com.sudox.android.di.module

import android.content.Context
import com.sudox.android.ApplicationLoader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideApplication(app : ApplicationLoader):Context = app
}