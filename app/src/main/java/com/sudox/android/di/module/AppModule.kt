package com.sudox.android.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.sudox.android.database.SudoxDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class AppModule {

    @Binds
    abstract fun providesApplication(app: Application): Context
}