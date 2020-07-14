package ru.sudox.android.core.ui.phone

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.telephony.PhoneNumberUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import com.google.android.material.button.MaterialButton
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.core.ui.R
import ru.sudox.android.countries.api.CountriesFeatureApi
import ru.sudox.phone.watchers.PhoneTextWatcher
import javax.inject.Inject

/**
 * Поле ввода номера телефона.
 */
@AndroidEntryPoint
class PhoneEditText : LinearLayout {

    private var regionCode: String? = null
    private var textWatcher: PhoneTextWatcher? = null
    private var countryCode: Int = 0

    @VisibleForTesting
    var countrySelectButton: MaterialButton

    @VisibleForTesting
    var phoneEditText: EditText

    @Inject
    lateinit var phoneNumberUtil: PhoneNumberUtil

    @Inject
    lateinit var countriesFeatureApi: CountriesFeatureApi

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.layout_phoneedittext, this, true)

        countrySelectButton = findViewById(R.id.countrySelectButton)
        phoneEditText = findViewById(R.id.phoneEditText)

        setBackgroundResource(R.drawable.drawable_edittext_background)
    }

    override fun onSaveInstanceState(): Parcelable = PhoneEditTextState(super.onSaveInstanceState()).also {
        it.regionCode = regionCode
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        (state as PhoneEditTextState).let {
            super.onRestoreInstanceState(state.superState)

            if (it.regionCode != null) {
                setCountry(it.regionCode!!)
            }
        }
    }

    /**
     * Устанавливает страну.
     * 1) Выбирается нужный флаг из диапазона Emoji
     * 2) Выбирается номер для отображения подсказки.
     * 3) Обновляется код страны.
     *
     * @param code Код страны.
     */
    @SuppressLint("SetTextI18n")
    fun setCountry(code: String) {
        if (textWatcher == null) {
            textWatcher = PhoneTextWatcher(phoneNumberUtil)
            phoneEditText.addTextChangedListener(textWatcher)
        }

        val exampleNumber = phoneNumberUtil.getExampleNumber(code)
        val countryCodeWithPlus = "+${exampleNumber.countryCode}"

        regionCode = code
        countryCode = exampleNumber.countryCode
        countrySelectButton.text = countryCodeWithPlus
        countrySelectButton.icon = countriesFeatureApi.getCountryFlag(code)
        textWatcher!!.setCountry(code, exampleNumber.countryCode)

        val phoneBuilder = StringBuilder()
        phoneNumberUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL, phoneBuilder)
        phoneBuilder.delete(0, countryCodeWithPlus.length + 1)
        phoneEditText.hint = phoneBuilder.toString()
    }

    /**
     * Выставляет слушатель кликов по кнопке выбора страны
     *
     * @param clickCallback Кэллбэк, который будет вызван при клике.
     */
    fun setCountrySelectButtonListener(clickCallback: () -> (Unit)) {
        countrySelectButton.setOnClickListener { clickCallback() }
    }

    /**
     * Возвращает введенный номер телефона.
     * Убирает разделители и + из кода страны.
     *
     * @return Номер телефона в строке
     */
    fun getEnteredPhoneNumber(): String = "$countryCode${PhoneNumberUtils.stripSeparators(phoneEditText.text.toString())}"
}