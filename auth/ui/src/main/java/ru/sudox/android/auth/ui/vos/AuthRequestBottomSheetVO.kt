package ru.sudox.android.auth.ui.vos

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.auth.ui.R
import ru.sudox.design.bottomsheet.vos.BottomSheetVO
import ru.sudox.design.buttons.createSecondaryButton
import ru.sudox.design.buttons.createPrimaryButton

class AuthRequestBottomSheetVO(
        val phoneName: String
) : BottomSheetVO {

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.new_device_wants_to_sign_in_to_your_account)
    }

    override fun getContentView(context: Context): View? {
        return AppCompatTextView(context).apply {
            text = phoneName

            setTextAppearance(this, R.style.Sudox_AuthRequestBottomSheet_PhoneNameTextAppearance)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_smartphone, 0, 0, 0)

            compoundDrawablePadding =
                    context.resources.getDimensionPixelSize(R.dimen.authrequestbottomsheet_margin_between_icon_and_phone_name)
        }
    }

    override fun getButtonsViews(context: Context): Array<View>? {
        return arrayOf(
                createPrimaryButton(context).apply { setText(R.string.accept) },
                createSecondaryButton(context).apply { setText(R.string.forbid) }
        )
    }
}