package ru.sudox.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Загрузчик приложения.
 */
@HiltAndroidApp
class AppLoader : Application()