package ru.sudox.android.people

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.sudox.android.core.controllers.ScrollableController
import ru.sudox.design.bottomsheet.BottomSheetLayout
import ru.sudox.design.bottomsheet.vos.BottomSheetVO
import ru.sudox.design.buttons.createDangerButton
import ru.sudox.design.buttons.createPrimaryButton

class ProfileController : ScrollableController() {

    override fun createChildView(container: ViewGroup, savedViewState: Bundle?): View {
        return View(activity)
    }

    override fun bindView(view: View) {
        super.bindView(view)

        BottomSheetDialog(activity!!).apply {
            setContentView(BottomSheetLayout(activity!!).apply {
                vo = object : BottomSheetVO {
                    override fun getTitle(context: Context): String? {
                        return "Новое устройство хочет авторизоваться в ваш аккаунт"
                    }

                    override fun getContentView(context: Context): View? {
                        return AppCompatTextView(context).apply { text = "Mi 8" }
                    }

                    override fun getButtonsViews(context: Context): Array<View>? {
                        return arrayOf(
                                createPrimaryButton(context).apply { text = "Accept" },
                                createDangerButton(context).apply { text = "Forbid" }
                        )
                    }
                }
            })
        }.show()
    }
}
