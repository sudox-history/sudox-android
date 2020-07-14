package ru.sudox.android.core.ui

import android.content.Context
import dagger.hilt.android.internal.testing.TestApplicationComponentManager
import dagger.hilt.android.internal.testing.TestApplicationComponentManagerHolder
import dagger.hilt.internal.GeneratedComponentManager

/**
 * Тестовый загрузчик приложения с поддержкой Hilt.
 */
class CommonUiHiltApplication : CommonUiApplication(), GeneratedComponentManager<Any>, TestApplicationComponentManagerHolder {

    private var componentManager: TestApplicationComponentManager? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        componentManager = TestApplicationComponentManager(this)
    }

    override fun componentManager(): Any = componentManager!!
    override fun generatedComponent(): Any = componentManager!!.generatedComponent()
}