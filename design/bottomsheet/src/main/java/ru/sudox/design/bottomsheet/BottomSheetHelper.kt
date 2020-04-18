package ru.sudox.design.bottomsheet

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.sudox.design.bottomsheet.vos.BottomSheetVO

fun createBottomSheetDialog(context: Context, vo: BottomSheetVO): BottomSheetDialog {
    return BottomSheetDialog(context).apply {
        setContentView(BottomSheetLayout(context).apply {
            this.vo = vo
        })
    }
}