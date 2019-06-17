package com.sudox.design.helpers

import android.graphics.Typeface
import android.util.SparseArray

internal const val HASH_CODE_PRIME_ODD = 31
internal val typefaces = SparseArray<Typeface>()

internal fun loadTypeface(name: String, style: Int = Typeface.NORMAL): Typeface {
    val typefaceHashCode = getTypefaceHashCode(name, style)
    var typeface = typefaces[typefaceHashCode]

    if (typeface == null) {
        typeface = Typeface.create(name, style)
        typefaces.append(typefaceHashCode, typeface)
    }

    return typeface
}

internal fun getTypefaceHashCode(name: String, style: Int): Int {
    return HASH_CODE_PRIME_ODD * name.hashCode() + style
}