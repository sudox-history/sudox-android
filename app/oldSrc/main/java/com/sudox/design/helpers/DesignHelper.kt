package com.sudox.design.helpers

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

@Suppress("DEPRECATION")
fun formatHtml(string: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(string)
    }
}

fun hideKeyboard(context: Context, view: View?) {
    if (view == null) return

    // Input manager
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    // Hide keyboard & remove focus
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ContextMenu.setOnItemClickListener(callback: (MenuItem) -> (Boolean)) {
    val size = size()

    // Установим слушатели
    for (i in 0 until size) {
        getItem(i).setOnMenuItemClickListener { callback(it) }
    }
}

/**
 * Генерирует строку из 1-х букв имени и фамилии
 *
 * Например:
 * Полное имя: Максим Митюшкин
 * Короткое: ММ
 **/
fun String.getTwoFirstLetters(): String {
    val builder = StringBuilder()
    val names = split(" ")

    // Билдим имя
    if (names.isNotEmpty() && names[0].isNotEmpty()) builder.append(names[0][0])
    if (names.size >= 2 && names[1].isNotEmpty()) builder.append(names[1][0])

    return builder.toString().toUpperCase()
}