package com.sudox.design.drawables.ripple;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({RippleMaskType.WITH_SPECIFIED_RADIUS, RippleMaskType.BORDERED, RippleMaskType.BORDERLESS})
public @interface RippleMaskType {
    int WITH_SPECIFIED_RADIUS = 0;
    int BORDERED = 1;
    int BORDERLESS = 2;
    int DEFAULT = BORDERED;
}
