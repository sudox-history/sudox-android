package ru.sudox.android.database.inject

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import ru.sudox.android.database.entities.auth.MyObjectBox
import javax.inject.Named
import javax.inject.Singleton

@Module
class DatabaseModule(
        private val name: String
) {

    @Provides
    @Singleton
    fun provideBoxStore(@Named(APP_CONTEXT_NAME) context: Context): BoxStore {
        return MyObjectBox
                .builder()
                .androidContext(context)
                .name(name)
                .build()
    }
}