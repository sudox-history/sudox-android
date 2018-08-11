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

    @Provides
    fun providesSudoxDatabase(context: Context) : SudoxDatabase =
            Room.databaseBuilder(context, SudoxDatabase::class.java, "sudox-database")
                    .allowMainThreadQueries().build()

    @Provides
    fun providesContactsDao(database: SudoxDatabase) = database.contactsDao()
}