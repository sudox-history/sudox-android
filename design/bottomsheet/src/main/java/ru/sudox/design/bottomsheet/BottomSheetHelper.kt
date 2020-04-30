package ru.sudox.design.bottomsheet

import android.content.Context
import ru.sudox.design.bottomsheet.vos.BottomSheetVO

/**
 * Создает BottomSheetDialog с нужным Layout'ом
 *
 * @param context Контекст активности
 * @param vo ViewObject, который поставляет параметры отображения BottomSheetDialog
 */
fun createBottomSheetDialog(context: Context, vo: BottomSheetVO): BottomSheetDialog {
    return BottomSheetDialog(context).apply {
        setContentView(BottomSheetLayout(context).apply {
            this.vo = vo
        })
    }
}