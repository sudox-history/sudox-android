package ru.sudox.design.buttons

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton

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
 * Создает кнопку, предупреждающую об опасности.
 *
 * @param context Контекст приложения/активности
 * @return Созданная View кнопки
 */
fun createDangerButton(context: Context): AppCompatButton {
    return AppCompatButton(ContextThemeWrapper(context, R.style.Sudox_Button_Danger), null, 0)
}