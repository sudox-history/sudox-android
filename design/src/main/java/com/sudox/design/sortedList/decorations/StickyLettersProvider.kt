package com.sudox.design.sortedList.decorations

import android.content.Context

interface StickyLettersProvider {
    fun getLetters(context: Context): HashMap<Int, String>
}