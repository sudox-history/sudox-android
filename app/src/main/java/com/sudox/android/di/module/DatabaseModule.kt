package com.sudox.android.di.module

import android.content.Context
import androidx.room.Room
import com.sudox.android.database.SudoxDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesSudoxDatabase(context: Context) = Room
            .databaseBuilder(context, SudoxDatabase::class.java, "sudox-database")
            .build()

    @Singleton
    @Provides
    fun providesContactsDao(database: SudoxDatabase) = database.contactsDao()
}