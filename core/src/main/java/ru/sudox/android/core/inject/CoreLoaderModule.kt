package ru.sudox.android.core.inject

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

const val APP_CONTEXT_NAME = "APP_CONTEXT"

@Module
class CoreLoaderModule(context: Context) {

    val context: Context = context
        @Provides
        @Singleton
        @Named(APP_CONTEXT_NAME)
        get
}