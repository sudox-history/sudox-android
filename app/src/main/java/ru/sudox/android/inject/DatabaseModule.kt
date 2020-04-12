package ru.sudox.android.inject

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.sudox.android.AppDatabase
import ru.sudox.android.auth.daos.AuthSessionDAO
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
class DatabaseModule(private val name: String) {

    @Provides
    @Singleton
    fun provideDatabase(@Named(APP_CONTEXT_NAME) context: Context): AppDatabase {
        return Room
                .databaseBuilder(context, AppDatabase::class.java, name)
                .build()
    }

    @Provides
    @Singleton
    fun provideAuthSessionDao(appDatabase: AppDatabase): AuthSessionDAO {
        return appDatabase.authSessionDao()
    }
}