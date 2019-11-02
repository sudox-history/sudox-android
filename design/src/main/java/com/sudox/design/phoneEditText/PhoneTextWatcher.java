package com.sudox.design.phoneEditText;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sudox.design.DesignLibraryKt;

import io.michaelrocks.libphonenumber.android.AsYouTypeFormatter;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

//@SuppressWarnings("ALL")
public class PhoneTextWatcher implements TextWatcher {

    /**
     * Indicates the change was caused by ourselves.
     */
    private boolean mSelfChange = false;
    /**
     * Indicates the formatting has been stopped.
     */
    private boolean mStopFormatting;
    private AsYouTypeFormatter mFormatter;
    private String countryNameCode;
    private int countryPhoneCode;

    @SuppressWarnings("ConstantConditions")
    public void setCountry(@NonNull String countryNameCode, int countryPhoneCode) {
        this.countryNameCode = countryNameCode;
        this.countryPhoneCode = countryPhoneCode;

        PhoneNumberUtil phoneNumberUtil = DesignLibraryKt.getPhoneNumberUtil();

        mFormatter = phoneNumberUtil.getAsYouTypeFormatter(countryNameCode);
        mFormatter.clear();
    }

    @Nullable
    public String getCountryNameCode() {
        return countryNameCode;
    }

    @Override
    public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(@NonNull Editable s) {
        synchronized (this) {
            int length1 = s.length();

            if (mStopFormatting) {
                // Restart the formatting when all texts were clear.
                mStopFormatting = !(length1 == 0);
                return;
            }
            if (mSelfChange) {
                // Ignore the change caused by s.replace().
                return;
            }

            //calculate few things that will be helpful later
            int selectionEnd = Selection.getSelectionEnd(s);
            boolean isCursorAtEnd = (selectionEnd == length1);

            //get formatted text for this number
            String formatted = reformat(s);

            if (formatted == null) {
                return;
            }

            //now calculate cursor position in formatted text
            int finalCursorPosition = 0;
            int length = formatted.length();
            if (formatted.equals(s.toString())) {
                //means there is no change while formatting don't move cursor
                finalCursorPosition = selectionEnd;
            } else if (isCursorAtEnd) {
                //if cursor was already at the end, put it at the end.
                finalCursorPosition = length;
            } else {

                // if no earlier case matched, we will use "digitBeforeCursor" way to figure out the cursor position
                int digitsBeforeCursor = 0;
                for (int i = 0; i < length1; i++) {
                    if (i >= selectionEnd) {
                        break;
                    }
                    if (PhoneNumberUtils.isNonSeparator(s.charAt(i))) {
                        digitsBeforeCursor++;
                    }
                }

                //at this point we will have digitsBeforeCursor calculated.
                // now find this position in formatted text
                for (int i = 0, digitPassed = 0; i < length; i++) {
                    if (digitPassed == digitsBeforeCursor) {
                        finalCursorPosition = i;
                        break;
                    }
                    if (PhoneNumberUtils.isNonSeparator(formatted.charAt(i))) {
                        digitPassed++;
                    }
                }
            }

            //if this ends right before separator, we might wish to move it further so user do not delete separator by mistake.
            // because deletion of separator will cause stop formatting that should not happen by mistake
            if (!isCursorAtEnd) {
                while (0 < finalCursorPosition - 1) {
                    if (PhoneNumberUtils.isNonSeparator(formatted.charAt(finalCursorPosition - 1)))
                        break;
                    finalCursorPosition--;
                }
            }

            //Now we have everything calculated, set this values in
            try {
                mSelfChange = true;
                s.replace(0, length1, formatted, 0, length);
                mSelfChange = false;
                Selection.setSelection(s, finalCursorPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * this will format the number in international format (only).
     */
    private String reformat(CharSequence s) {
        if (mFormatter == null) {
            return null;
        }

        String internationalFormatted = "";
        mFormatter.clear();
        char lastNonSeparator = 0;

        String countryCallingCode = "+" + countryPhoneCode;
        s = countryCallingCode + s;
        int len = s.length();

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (PhoneNumberUtils.isNonSeparator(c)) {
                if (lastNonSeparator != 0) {
                    internationalFormatted = mFormatter.inputDigit(lastNonSeparator);
                }
                lastNonSeparator = c;
            }
        }
        if (lastNonSeparator != 0) {
            internationalFormatted = mFormatter.inputDigit(lastNonSeparator);
        }

        internationalFormatted = internationalFormatted.trim();

        if (internationalFormatted.length() > countryCallingCode.length()) {
            if (internationalFormatted.charAt(countryCallingCode.length()) == ' ') {
                internationalFormatted = internationalFormatted.substring(countryCallingCode.length() + 1);
            } else {
                internationalFormatted = internationalFormatted.substring(countryCallingCode.length());
            }
        } else {
            internationalFormatted = "";
        }

        return TextUtils.isEmpty(internationalFormatted) ? "" : internationalFormatted;
    }
}