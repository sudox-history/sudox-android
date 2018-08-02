package com.sudox.android.di.module

import android.app.Application
import android.content.Context
import com.sudox.android.ApplicationLoader
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    abstract fun provideApplication(app: Application): Context
}