package com.sudox.design.drawables.ripple;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({RippleMaskType.BORDERED, RippleMaskType.BORDERLESS})
public @interface RippleMaskType {
    int BORDERED = 0;
    int BORDERLESS = 1;
    int DEFAULT = BORDERED;
}
