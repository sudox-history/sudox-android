package com.sudox.design.lists.sortedList.decorations

import android.content.Context

interface StickyLettersProvider {
    fun getLetters(context: Context): HashMap<Int, String>
}