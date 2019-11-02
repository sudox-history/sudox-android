package com.sudox.design.phoneEditText;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.sudox.design.DesignLibraryKt;

import io.michaelrocks.libphonenumber.android.AsYouTypeFormatter;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

@SuppressWarnings("ALL")
public class PhoneTextWatcher implements TextWatcher {

    private static final String TAG = "Int'l Phone TextWatcher";
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
    Editable lastFormatted = null;
    private int countryPhoneCode;

    //when country is changed, we update the number.
    //at this point this will avoid "stopFormatting"
    private boolean needUpdateForCountryChange = false;
    private boolean internationalOnly = false;

    public void setCountry(String countryNameCode, int countryPhoneCode) {
        this.countryNameCode = countryNameCode;
        this.countryPhoneCode = countryPhoneCode;

        PhoneNumberUtil phoneNumberUtil = DesignLibraryKt.getPhoneNumberUtil();

        mFormatter = phoneNumberUtil.getAsYouTypeFormatter(countryNameCode);
        mFormatter.clear();
        if (lastFormatted != null) {
            needUpdateForCountryChange = true;
            String onlyDigits = phoneNumberUtil.normalizeDigitsOnly(lastFormatted);
            lastFormatted.replace(0, lastFormatted.length(), onlyDigits, 0, onlyDigits.length());
            needUpdateForCountryChange = false;
        }
    }

    public String getCountryNameCode() {
        return countryNameCode;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mSelfChange || mStopFormatting) {
            return;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mSelfChange || mStopFormatting) {
            return;
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
        if (mStopFormatting) {
            // Restart the formatting when all texts were clear.
            mStopFormatting = !(s.length() == 0);
            return;
        }
        if (mSelfChange) {
            // Ignore the change caused by s.replace().
            return;
        }

        //calculate few things that will be helpful later
        int selectionEnd = Selection.getSelectionEnd(s);
        boolean isCursorAtEnd = (selectionEnd == s.length());

        //get formatted text for this number
        String formatted = reformat(s);

        if (formatted == null) {
            return;
        }

        //now calculate cursor position in formatted text
        int finalCursorPosition = 0;
        if (formatted.equals(s.toString())) {
            //means there is no change while formatting don't move cursor
            finalCursorPosition = selectionEnd;
        } else if (isCursorAtEnd) {
            //if cursor was already at the end, put it at the end.
            finalCursorPosition = formatted.length();
        } else {

            // if no earlier case matched, we will use "digitBeforeCursor" way to figure out the cursor position
            int digitsBeforeCursor = 0;
            for (int i = 0; i < s.length(); i++) {
                if (i >= selectionEnd) {
                    break;
                }
                if (PhoneNumberUtils.isNonSeparator(s.charAt(i))) {
                    digitsBeforeCursor++;
                }
            }

            //at this point we will have digitsBeforeCursor calculated.
            // now find this position in formatted text
            for (int i = 0, digitPassed = 0; i < formatted.length(); i++) {
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
            while (0 < finalCursorPosition - 1 && !PhoneNumberUtils.isNonSeparator(formatted.charAt(finalCursorPosition - 1))) {
                finalCursorPosition--;
            }
        }

        //Now we have everything calculated, set this values in
        try {
            if (formatted != null) {
                mSelfChange = true;
                s.replace(0, s.length(), formatted, 0, formatted.length());
                mSelfChange = false;
                lastFormatted = s;
                Selection.setSelection(s, finalCursorPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        if (internationalOnly || (s.length() > 0 && s.charAt(0) != '0'))
            //to have number formatted as international format, add country code before that
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
        if (internationalOnly || (s.length() == 0 || s.charAt(0) != '0')) {
            if (internationalFormatted.length() > countryCallingCode.length()) {
                if (internationalFormatted.charAt(countryCallingCode.length()) == ' ')
                    internationalFormatted = internationalFormatted.substring(countryCallingCode.length() + 1);
                else
                    internationalFormatted = internationalFormatted.substring(countryCallingCode.length());
            } else {
                internationalFormatted = "";
            }
        }

        return TextUtils.isEmpty(internationalFormatted) ? "" : internationalFormatted;
    }

    private void stopFormatting() {
        mStopFormatting = true;
        mFormatter.clear();
    }

    private boolean hasSeparator(final CharSequence s, final int start, final int count) {
        for (int i = start; i < start + count; i++) {
            char c = s.charAt(i);

            if (!PhoneNumberUtils.isNonSeparator(c)) {
                return true;
            }
        }

        return false;
    }
}