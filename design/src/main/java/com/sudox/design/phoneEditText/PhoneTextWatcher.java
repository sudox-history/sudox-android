package com.sudox.design.phoneEditText;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.sudox.design.DesignLibraryKt;

import java.util.Objects;

import io.michaelrocks.libphonenumber.android.AsYouTypeFormatter;

/**
 * Watches a {@link android.widget.TextView} and if a phone number is entered
 * will format it.
 * <p/>
 * Stop formatting when the user
 * <ul>
 * <li>Inputs non-dialable characters</li>
 * <li>Removes the separator in the middle of string.</li>
 * </ul>
 * <p/>
 * The formatting will be restarted once the text is cleared.
 */
@RestrictTo({RestrictTo.Scope.LIBRARY})
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
    private String mRegionCode;

    public void setRegionCode(@NonNull String regionCode) {
        mSelfChange = false;
        mStopFormatting = false;
        mRegionCode = regionCode;

        mFormatter = Objects.requireNonNull(DesignLibraryKt
                .getPhoneNumberUtil())
                .getAsYouTypeFormatter(regionCode);
    }

    @Nullable
    @SuppressWarnings("UnusedReturnValue")
    public String getRegionCode() {
        return mRegionCode;
    }

    @Override
    public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
        if (mSelfChange || mStopFormatting) {
            return;
        }

        // If the user manually deleted any non-dialable characters, stop formatting
        if (count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
        if (mSelfChange || mStopFormatting) {
            return;
        }

        // If the user inserted any non-dialable characters, stop formatting
        if (count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override
    public void afterTextChanged(@NonNull Editable s) {
        if (mStopFormatting) {
            // Restart the formatting when all texts were clear.
            mStopFormatting = !(s.length() == 0);
            return;
        }

        if (mSelfChange) {
            // Ignore the change caused by s.replace().
            return;
        }

        String formatted = reformat(s, Selection.getSelectionEnd(s));

        if (formatted != null) {
            int rememberedPos = mFormatter.getRememberedPosition();
            mSelfChange = true;
            s.replace(0, s.length(), formatted, 0, formatted.length());

            if (formatted.equals(s.toString())) {
                Selection.setSelection(s, rememberedPos);
            }

            mSelfChange = false;
        }
    }

    /**
     * Generate the formatted number by ignoring all non-dialable chars and stick the cursor to the
     * nearest dialable char to the left. For instance, if the number is  (650) 123-45678 and '4' is
     * removed then the cursor should be behind '3' instead of '-'.
     */
    @Nullable
    private String reformat(@NonNull CharSequence s, int cursor) {
        if (mFormatter == null) {
            return null;
        }

        // The index of char to the leftward of the cursor.
        int curIndex = cursor - 1;
        String formatted = null;
        mFormatter.clear();

        char lastNonSeparator = 0;
        boolean hasCursor = false;
        int len = s.length();

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            if (PhoneNumberUtils.isNonSeparator(c)) {
                if (lastNonSeparator != 0) {
                    formatted = getFormattedNumber(lastNonSeparator, hasCursor);
                    hasCursor = false;
                }

                lastNonSeparator = c;
            }

            if (i == curIndex) {
                hasCursor = true;
            }
        }

        if (lastNonSeparator != 0) {
            formatted = getFormattedNumber(lastNonSeparator, hasCursor);
        }

        return formatted;
    }

    private String getFormattedNumber(char lastNonSeparator, boolean hasCursor) {
        return hasCursor ? mFormatter.inputDigitAndRememberPosition(lastNonSeparator)
                : mFormatter.inputDigit(lastNonSeparator);
    }

    private void stopFormatting() {
        mStopFormatting = true;
        mFormatter.clear();
    }

    private static boolean hasSeparator(@NonNull final CharSequence s, final int start, final int count) {
        for (int i = start; i < start + count; i++) {
            char c = s.charAt(i);

            if (!PhoneNumberUtils.isNonSeparator(c)) {
                return true;
            }
        }

        return false;
    }
}