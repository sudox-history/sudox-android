package com.sudox.design.helpers

import android.graphics.Typeface
import android.util.SparseArray

private const val HASH_CODE_PRIME_ODD = 31
private val typefaces = SparseArray<Typeface>()

fun loadTypeface(name: String, style: Int = Typeface.NORMAL): Typeface {
    val typefaceHashCode = getTypefaceHashCode(name, style)
    var typeface = typefaces[typefaceHashCode]

    if (typeface == null) {
        typeface = Typeface.create(name, style)
        typefaces.append(typefaceHashCode, typeface)
    }

    return typeface
}

private fun getTypefaceHashCode(name: String, style: Int): Int {
    return HASH_CODE_PRIME_ODD * name.hashCode() + style
}