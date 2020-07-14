package ru.sudox.android.core.ui

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Стартер тестов.
 * Устанавливает конфигурацию тестов.
 */
class CommonUiRunner(
    testClass: Class<*>
) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder()
            .setApplication(CommonUiApplication::class.java)
            .setPackageName("ru.sudox.android.core.ui")
            .build()
    }
}