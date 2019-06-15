package com.sudox.android.common.di.module

import androidx.room.Room
import android.content.Context
import com.sudox.android.data.database.SudoxDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesSudoxDatabase(context: Context) = Room
            .databaseBuilder(context, SudoxDatabase::class.java, "sudox-database")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providesContactsDao(database: SudoxDatabase) = database.userDao()

    @Singleton
    @Provides
    fun providesMessagesDao(database: SudoxDatabase) = database.messagesDao()
}