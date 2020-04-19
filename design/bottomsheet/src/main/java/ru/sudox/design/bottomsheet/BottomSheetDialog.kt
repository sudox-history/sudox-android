package ru.sudox.design.bottomsheet

import android.content.Context
import android.content.DialogInterface
import android.view.View

class BottomSheetDialog : com.google.android.material.bottomsheet.BottomSheetDialog {

    constructor(context: Context) : super(context)
    constructor(context: Context, theme: Int) : super(context, theme)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener)

    override fun setContentView(view: View) {
        super.setContentView(view)

        val frameLayout = (view.parent as View).apply {
            fitsSystemWindows = true
        }

        // P.S.: Смотрите design_bottom_sheet_dialog.xml

        (frameLayout
                .parent // CoordinatorLayout
                .parent as? View)?.fitsSystemWindows = false // FrameLayout (с выставленным fitsSystemWindow = true)
    }
}