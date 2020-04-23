package ru.sudox.design.buttons.helpers

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import ru.sudox.design.buttons.R

/**
 * Создает кнопку первого применения.
 *
 * @param context Контекст приложения/активности
 * @return Созданная View кнопки
 */
fun createPrimaryButton(context: Context): AppCompatButton {
    return AppCompatButton(ContextThemeWrapper(context, R.style.Sudox_Button_Primary), null, 0)
}

/**
 * Создает кнопку второго применения.
 *
 * @param context Контекст приложения/активности
 * @return Созданная View кнопки
 */
fun createSecondaryButton(context: Context): AppCompatButton {
    return AppCompatButton(ContextThemeWrapper(context, R.style.Sudox_Button_Secondary), null, 0)
}