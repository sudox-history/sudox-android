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
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@Suppress("DEPRECATION")
fun formatHtml(string: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(string)
    }
}

fun hideKeyboard(context: Context, view: View) {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    // Hide keyboard & remove focus
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun drawAvatar(text: String, firstColor: String, secondColor: String, textColor: Int = Color.WHITE): Bitmap {
    val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val formattedText = text.toUpperCase()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Draw gradient
    paint.shader = LinearGradient(0F, 0F, 200F, 200F, Color.parseColor(firstColor), Color.parseColor(secondColor), Shader.TileMode.REPEAT)

    // Draw circle
    canvas.drawRect(0F, 0F, 200F, 200F, paint)

    // Text bounds
    val textRect = Rect()

    // Draw text
    paint.shader = null
    paint.color = textColor
    paint.textSize = 60F
    paint.getTextBounds(formattedText, 0, formattedText.length, textRect)
    canvas.drawText(formattedText, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

    return bitmap
}

fun ContextMenu.setOnItemClickListener(callback: (MenuItem) -> (Boolean)) {
    val size = size()

    // Установим слушатели
    for (i in 0 until size) {
        getItem(i).setOnMenuItemClickListener { callback(it) }
    }
}

fun drawCircleBitmap(context: Context, bitmap: Bitmap, view: ImageView) {
    Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions.circleCropTransform())
            .into(view)
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
    if (names.isNotEmpty()) builder.append(names[0][0])
    if (names.size >= 2) builder.append(names[1][0])

    return builder.toString()
}