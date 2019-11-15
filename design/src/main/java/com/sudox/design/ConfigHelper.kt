package com.sudox.design

import android.content.res.Configuration
import android.os.Build
import java.util.Locale

@Suppress("DEPRECATION")
fun Configuration.getLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales[0]
    } else {
        locale
    }
}