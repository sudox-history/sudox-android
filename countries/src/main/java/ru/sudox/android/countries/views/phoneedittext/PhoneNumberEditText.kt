package ru.sudox.android.countries.views.phoneedittext

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.autofill.AutofillValue
import androidx.annotation.RequiresApi
import ru.sudox.design.edittext.BasicEditText

class PhoneNumberEditText : BasicEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun autofill(value: AutofillValue) {
        (parent as PhoneEditText).phoneNumber = value.textValue.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAutofillValue(): AutofillValue? {
        val parent = parent as? PhoneEditText ?: return null
        val phoneNumber = parent.phoneNumber ?: return null

        return AutofillValue.forText("+${phoneNumber}")
    }
}