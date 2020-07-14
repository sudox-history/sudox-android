package ru.sudox.android.core.ui

import android.app.Application

/**
 * Тестовый загрузчик приложения.
 * Устанавливает тему по-умолчанию.
 */
open class CommonUiApplication : Application() {

    override fun onCreate() {
        setTheme(R.style.Theme_Sudox)
        super.onCreate()
    }
}