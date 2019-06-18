package com.sudox.messenger.android.core.fragment;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({AppFragmentType.AUTH, AppFragmentType.MAIN})
public @interface AppFragmentType {
    int AUTH = 0;
    int MAIN = 1;
}